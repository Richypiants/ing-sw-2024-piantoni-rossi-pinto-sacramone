package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.*;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

//FIXME: abstract with static methods or singleton? METTERE GLI OVERRIDE!
//TODO:  In every custom exception write in the constructor call a string verbosely describing the event that caused it and why

/*TODO: In case of high traffic volumes on network, we can reduce it by sending the updates to lobby states (creation, updates) only to clients
        which aren't already in a lobby. */
public class ServerController implements ServerControllerInterface {

    private static final ServerController SINGLETON_INSTANCE = new ServerController();

    public final Map<Integer, Card> cardsList;
    public final Map<Integer, ClientCard> clientCardsList;
    public final Map<VirtualClient, Player> players;
    public final Map<UUID, GameLobby> lobbiesAndGames;
    public final Map<Player, GameLobby> playersToLobbiesAndGames;
    public final Map<VirtualClient, TimerTask> timeoutTasks;
    public final long TIMEOUT_TASK_EXECUTION_AFTER = 30000;

    private ServerController() {
        cardsList = loadModelCards();
        clientCardsList = loadClientCards();
        players = new HashMap<>();
        lobbiesAndGames = new HashMap<>();
        playersToLobbiesAndGames = new HashMap<>();
        timeoutTasks = new HashMap<>();
    }

    public static ServerController getInstance() {
        return SINGLETON_INSTANCE;
    }

    private Map<Integer, Card> loadModelCards() {
        //TODO: map of maps?
        Map<Integer, Card> tmp = new HashMap<>();
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("resource_cards.json",
                        new TypeToken<ArrayList<ResourceCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json",
                        new TypeToken<ArrayList<GoldCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json",
                        new TypeToken<ArrayList<InitialCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json",
                        new TypeToken<ArrayList<ObjectiveCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));

        return Collections.unmodifiableMap(tmp);
    }

    private Map<Integer, ClientCard> loadClientCards(){
        return JSONParser.clientCardsFromJSON("client_cards.json")
                .stream().collect(Collectors.toMap((card) -> card.ID, (card) -> card));
    }

    private boolean hasNoPlayer(VirtualClient client) {
        if (!players.containsKey(client)) {
            requestToClient(
                    client,
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Unregistered client")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    //FIXME: abbastanza orribile e duplicato... playerState?
    private boolean inGame(VirtualClient client, boolean throwIf) {
        if (players.get(client) instanceof InGamePlayer == throwIf) {
            requestToClient(
                    client,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot execute action while " +
                                    (throwIf ? "" : "not ") + "in a game")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    private boolean inLobbyOrGame(VirtualClient client, boolean throwIf) {
        if (playersToLobbiesAndGames.containsKey(players.get(client)) == throwIf) {
            requestToClient(
                    client,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot execute action while " +
                                    (throwIf ? "" : "not ") + "in a lobby or in a game")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    private boolean validCard(VirtualClient sender, int cardID) {
        if(!cardsList.containsKey(cardID)){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            ); //throwException();
            return false;
        }
        return true;
    }

    //Helper method to catch RemoteException (and eventually other ones) only one time
    public void requestToClient(VirtualClient client, ClientCommand command) {
        try {
            client.requestToClient(command);
        } catch (IOException e) {
            //If communication is closed, the target has lost an update, so in case he reconnects, its game is inconsistent, we must send the update,
            //so the TimeoutTask routine has to be instantly executed.
            timeoutTasks.get(client).run();
            timeoutTasks.get(client).cancel();
            timeoutTasks.remove(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renewTimeoutTimerTask(VirtualClient target){
        Timer timer = new Timer(true);
        TimerTask timeoutTask = new TimerTask() {
            @Override
            public void run() {
                disconnectionRoutine(target);
            }
        };
        timer.schedule(timeoutTask, TIMEOUT_TASK_EXECUTION_AFTER);

        timeoutTasks.put(target, timeoutTask);
    }

    private void disconnectionRoutine(VirtualClient target){
        System.out.println("[SERVER] Removing the entry of " + target + " since it didn't send any keepAlive in " + TIMEOUT_TASK_EXECUTION_AFTER/1000
                + " seconds or the game has sent an update and its state is inconsistent.");
        Player thisPlayer = players.get(target);
        if(playersToLobbiesAndGames.containsKey(thisPlayer)){
            GameLobby thisGame = playersToLobbiesAndGames.get(thisPlayer);
            if(thisGame instanceof Game)
                leaveGame(target);
            else
                leaveLobby(target, true);
        }
    }

    public void createPlayer(VirtualClient sender, String nickname) {
        System.out.println("[CLIENT]: CreatePlayerCommand received and being executed");
        if (players.containsKey(sender)) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Client already registered")
                    )
            ); //throwException();
            return;
        }

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if (selectedPlayer.isPresent()) {
            System.out.println("[SERVER]: sending an Exception while trying to log in to " + sender);
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            ); //throwException();
        } else {
            Optional<Player> selectedGamePlayer = playersToLobbiesAndGames.keySet().stream()
                    .filter((player) -> player.getNickname().equals(nickname))
                    .findAny();

            if (selectedGamePlayer.isPresent()) {
                Player target = selectedGamePlayer.get();
                Game targetGame = (Game) playersToLobbiesAndGames.get(target);
                players.put(sender, target);

                System.out.println("[SERVER]: sending SetNicknameCommand and RestoreGameCommand to client " + sender);
                requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();

                if(targetGame.getCurrentState() instanceof AwaitingReconnectionState)
                    //If game was in AwaitingReconnectingState, you need to resume it before sending the DTO
                    ((AwaitingReconnectionState) targetGame.getCurrentState()).recoverGame();

                requestToClient(sender, new RestoreGameCommand(
                        targetGame.generateDTO((InGamePlayer) target),
                        targetGame.getCurrentState().getStringEquivalent(), //To let the client understand in which state it has to be recovered to.
                        targetGame.generateTemporaryFieldsToPlayers() //fields related to the players inGame.
                )); //restoreGame();

                for (var player : targetGame.getActivePlayers())
                    if (player.isActive()) {
                        VirtualClient targetClient = keyReverseLookup(players, player::equals);
                        requestToClient(targetClient, new ToggleActiveCommand(nickname)); //toggleActive()
                    }

                ((InGamePlayer) target).toggleActive();
                //FIXME: restoreGame va chiamata anche quando non c'è il gioco ma il file salvato perchè il server era crashato
            } else {
                Player target = new Player(nickname);
                players.put(sender, target);
                System.out.println("[SERVER]: sending SetNicknameCommand and SetLobbiesCommand to client " + sender);
                requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();
                requestToClient(
                        sender,
                        new SetLobbiesCommand(
                                lobbiesAndGames.entrySet().stream()
                                        .filter((entry) -> !(entry.getValue() instanceof Game))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                        )
                ); //setLobbies();
            }
        }

        //Creating the timeoutRoutine that will be started in case the client doesn't send a keepAliveCommand in the 60 seconds span.
        renewTimeoutTimerTask(sender);
    }

    public void setNickname(VirtualClient sender, String nickname) {
        System.out.println("[CLIENT]: SetNicknameCommand received and being executed");
        if (hasNoPlayer(sender) || inLobbyOrGame(sender, true)) return;

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if(selectedPlayer.isPresent()) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            ); //throwException();
        } else {
            players.get(sender).setNickname(nickname);
            System.out.println("[SERVER]: sending SetNicknameCommand to client " + sender);
            requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();

            //TODO: update to other players too!
        }
    }

    public void keepAlive(VirtualClient sender) {
        if(hasNoPlayer(sender)) return;

        if(timeoutTasks.containsKey(sender)) {
            timeoutTasks.get(sender).cancel();
            timeoutTasks.remove(sender);
            renewTimeoutTimerTask(sender);
        }
        System.out.println("[CLIENT]: keepAlive command received from " + sender + ". Resetting timeout");
    }

    public void createLobby(VirtualClient sender, int maxPlayers) {
        System.out.println("[CLIENT]: CreateLobbyCommand received and being executed");
        if (hasNoPlayer(sender) || inLobbyOrGame(sender, true)) return;
        //TODO: si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        if(maxPlayers < 2 || maxPlayers > 4){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid number of max players (out of range: accepted [2-4])")
                    )
            ); //throwException();
            return;
        }

        Player target = players.get(sender);
        GameLobby lobby = new GameLobby(target, maxPlayers);
        UUID lobbyUUID;

        do {
            lobbyUUID = UUID.randomUUID();
        } while (lobbiesAndGames.containsKey(lobbyUUID));

        lobbiesAndGames.put(lobbyUUID, lobby);
        playersToLobbiesAndGames.put(target, lobby);

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        for(var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    public void joinLobby(VirtualClient sender, UUID lobbyUUID) {
        System.out.println("[CLIENT]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender) || inLobbyOrGame(sender, true)) return;

        if (!lobbiesAndGames.containsKey(lobbyUUID)) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            ); //throwException();
            return;
        }

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);

        if(lobby instanceof Game){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("The provided UUID refers to a game and not to a lobby")
                    )
            ); //throwException();
            return;
        }

        if(lobby.getPlayersNumber() >= lobby.getMaxPlayers()){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot join a full lobby")
                    )
            ); //throwException();
            return;
        }

        Player target = players.get(sender);

        lobby.addPlayer(target);
        playersToLobbiesAndGames.put(target, lobby);

        //TODO: startGame()... && add synchronization
        if(lobby.getPlayersNumber() >= lobby.getMaxPlayers()) {
            Game newGame = new Game(lobby);

            lobbiesAndGames.put(lobbyUUID, newGame);

            System.out.println("[SERVER]: sending StartGameCommand to clients starting game");
            //TODO: estrarre la logica di evoluzione dei player da Game (altrimenti, fixare i get) E SINCRONIZZAREEEE
            for(var player : lobby.getPlayers()){
                VirtualClient targetClient = keyReverseLookup(players, player::equals);
                InGamePlayer targetInGamePlayer = newGame.getPlayers().stream()
                        .filter((inGamePlayer) -> inGamePlayer.getNickname().equals(player.getNickname()))
                        .findFirst()
                        .orElseThrow(); //TODO: strano... gestire?

                players.put(targetClient, targetInGamePlayer);
                playersToLobbiesAndGames.remove(player);
                playersToLobbiesAndGames.put(targetInGamePlayer, newGame);

                requestToClient(targetClient, new StartGameCommand(lobbyUUID, newGame.generateDTO(targetInGamePlayer)));
                //startGame();

                //FIXME: should clients inform that they are ready before? (ready() method call?)
                //Calls to game creation, generateInitialCards ...
            }
            newGame.getCurrentState().transition();

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & GameLobby?
            while(lobby.getPlayersNumber() > 0) {
                lobby.removePlayer(lobby.getPlayers().getFirst());
            }
        }

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for(var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    public void leaveLobby(VirtualClient sender, boolean isInactive) {
        System.out.println("[CLIENT]: LeaveLobbyCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, true) || inLobbyOrGame(sender, false)) return;

        Player target = players.get(sender);

        GameLobby lobby = playersToLobbiesAndGames.get(target);
        UUID lobbyUUID = keyReverseLookup(lobbiesAndGames, lobby::equals);
        //Assuming that lobby is contained (thus maps are coherent): check with synchronization that this
        // invariant holds

        lobby.removePlayer(target);
        playersToLobbiesAndGames.remove(target);

        if (lobby.getPlayers().isEmpty()) {
            lobbiesAndGames.remove(lobbyUUID);
        }

        if(isInactive)
            players.remove(sender);

        System.out.println("[SERVER]: sending UpdateLobbiesCommand to clients");

        for(var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); // updateLobby();
    }

    public void pickObjective(VirtualClient sender, int cardID) {
        System.out.println("[CLIENT]: PickObjectiveCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        if(!cardsList.containsKey(cardID)){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            ); //throwException();
            return;
        }

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);
        Card targetCard = cardsList.get(cardID);

        if(targetCard instanceof ObjectiveCard)
            try{
                targetGame.getCurrentState().pickObjective(targetPlayer, (ObjectiveCard) targetCard);
                //TODO: maybe send a response back to the player?
            } catch(ForbiddenActionException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot pick an objective card in this state")
                        )
                ); //throwException();
            } catch (CardNotInHandException e){
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                ); //throwException();
            } catch (AlreadySetCardException e){
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new AlreadySetCardException("Secret objective already chosen")
                        )
                ); //throwException();
            }
        else {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Card with provided cardID is not of type ObjectiveCard")
                    )
            ); //throwException();
        }
    }

    public void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        System.out.println("[CLIENT]: PlaceCardCommand received and being executed");

        if (hasNoPlayer(sender) || inGame(sender, false) || !validCard(sender, cardID)) return;

        if(Arrays.stream(Side.values()).noneMatch((side) -> side.equals(playedSide))){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid card side")
                    )
            ); //throwException();
            return;
        }

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);
        Card targetCard = cardsList.get(cardID);

        if(targetCard instanceof PlayableCard)
            try{
                targetGame.getCurrentState().placeCard(targetPlayer, coordinates, (PlayableCard) targetCard, playedSide);
            } catch(ForbiddenActionException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot place a card in this state")
                        )
                ); //throwException();
            } catch(UnexpectedPlayerException e){
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Not this player's turn")
                        )
                ); //throwException();
            } catch (CardNotInHandException e){
                requestToClient(sender,
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                ); //throwException();
            } catch (NotEnoughResourcesException e){
                requestToClient(sender,
                        new ThrowExceptionCommand(
                                new NotEnoughResourcesException(
                                        "Player doesn't own the required resources to play the provided card"
                                )
                        )
                ); //throwException();
            } catch (InvalidCardPositionException e){
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new InvalidCardPositionException("Provided coordinates are not valid for placing a card")
                        )
                ); //throwException();
            }
        else {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Provided card is not of a playable type")
                    )
            ); //throwException();
        }
    }

    public void drawFromDeck(VirtualClient sender, String deck) {
        System.out.println("[CLIENT]: DrawFromDeckCommand received and being executed");

        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        try{
            targetGame.getCurrentState().drawFrom(targetPlayer, deck);
        } catch(ForbiddenActionException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a card from a deck in this state")
                    )
            ); //throwException();
        } catch (UnexpectedPlayerException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
        } catch (UnknownStringException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such deck exists")
                    )
            ); //throwException();
        } catch (EmptyDeckException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new EmptyDeckException("Selected deck is empty")
                    )
            ); //throwException();
        }
    }

    public void drawFromVisibleCards(VirtualClient sender, String deck, int position) {
        System.out.println("[CLIENT]: DrawFromVisibleCardsCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        try{
            targetGame.getCurrentState().drawFrom(targetPlayer, deck, position);
        } catch(ForbiddenActionException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a visible card in this state")
                    )
            ); //throwException();
        } catch (UnexpectedPlayerException e){
            requestToClient(sender,
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
        } catch (InvalidDeckPositionException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidDeckPositionException("Cannot understand which card to draw")
                    )
            ); //throwException();
        } catch (UnknownStringException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such placed cards exist")
                    )
            ); //throwException();
        } catch (EmptyDeckException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new EmptyDeckException("No card in selected slot")
                    )
            ); //throwException();
        }
    }

    public void leaveGame(VirtualClient sender) {
        System.out.println("[CLIENT]: LeaveGameCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        targetPlayer.toggleActive();
        players.remove(sender);

        /*Checking if the disconnection happened during the sender turn. If so:
        * 1. If it was during PlayerTurnPlayState,
        *   the game will transition() to the PlayerTurnDrawState
        *   that will check if the player is inactive and then transition as well.
        *
        * 2. If it was during PlayerTurnDrawState,
        *    a card has to be drawn following a standard routine, if no card can be drawn, transition()
        *    without giving any card.
        *
        * If the player disconnected in another player's turn, there's no problem
        * because the players' activity is managed by the GameStates.
        * */

        if(targetGame.getCurrentState().getCurrentPlayer() == null || targetGame.getCurrentState().getCurrentPlayer().equals(targetPlayer))
            targetGame.getCurrentState().playerDisconnected(targetPlayer);

        System.out.println("[SERVER]: sending ToggleActiveCommand to clients");

        for(var player : targetGame.getActivePlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            requestToClient(targetClient, new ToggleActiveCommand(player.getNickname()));
        }

        int activePlayers = targetGame.getActivePlayers().size();
        if(activePlayers == 1){
            for(var player : targetGame.getActivePlayers())
                requestToClient(keyReverseLookup(players, player::equals), new PauseGameCommand());

            targetGame.setState(new AwaitingReconnectionState(targetGame));
            System.out.println("[SERVER]: Freezing " + targetGame.toString() + " game");
        } else if(activePlayers == 0){
            ((AwaitingReconnectionState) targetGame.getCurrentState()).cancelTimerTask();
            for(var player: targetGame.getPlayers())
                playersToLobbiesAndGames.remove(player);
            lobbiesAndGames.remove(targetGame);
        }
    }

    public void broadcastMessage(VirtualClient sender, String message) {
        System.out.println("[CLIENT]: BroadcastMessageCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        System.out.println("[SERVER]: sending AddChatMessageCommand to clients");
        for (var inGamePlayer : ((Game) playersToLobbiesAndGames.get(players.get(sender))).getPlayers())
            if (inGamePlayer.isActive())
                requestToClient(
                        keyReverseLookup(players, inGamePlayer::equals),
                        new AddChatMessageCommand(senderPlayer.getNickname(), message, false)
                );
    }

    public void directMessage(VirtualClient sender, String receiverNickname, String message) {
        System.out.println("[CLIENT]: DirectMessageCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(receiverNickname))
                .findAny();

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        if(selectedPlayer.isPresent()) {
            Player receiverPlayer = selectedPlayer.get();
            if(playersToLobbiesAndGames.get(players.get(sender)).equals(playersToLobbiesAndGames.get(receiverPlayer))) {
                if (((InGamePlayer) receiverPlayer).isActive()) {
                    System.out.println("[SERVER]: sending AddChatMessageCommand to sender and target client");
                    requestToClient(
                            sender,
                            new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                    );
                    requestToClient(
                            keyReverseLookup(players, receiverPlayer::equals),
                            new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                    );
                } else requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Nickname provided has no active player associated in this game")
                        )
                );
            } else requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Nickname provided has no associated player in this game")
                    )
            );
        } else requestToClient(
                sender,
                new ThrowExceptionCommand(
                        new NotExistingPlayerException("Nickname provided has no associated player registered")
                )
        );
    }
}