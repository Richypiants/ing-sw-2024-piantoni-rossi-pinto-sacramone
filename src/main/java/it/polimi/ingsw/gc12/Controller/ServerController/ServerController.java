package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.*;
import it.polimi.ingsw.gc12.Controller.Commands.KeepAliveCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.*;

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
    public final Map<VirtualClient, Player> players;
    public final Map<UUID, GameLobby> lobbiesAndGames;
    public final Map<Player, GameLobby> playersToLobbiesAndGames;

    private ServerController() {
        cardsList = loadCards();
        players = new HashMap<>();
        lobbiesAndGames = new HashMap<>();
        playersToLobbiesAndGames = new HashMap<>();
    }

    public static ServerController getInstance() {
        return SINGLETON_INSTANCE;
    }

    private Map<Integer, Card> loadCards() {
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
    private void requestToClient(VirtualClient client, ClientCommand command) {
        try {
            client.requestToClient(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(VirtualClient sender, String nickname) {
        System.out.println("[CLIENT]: CreatePlayerCommand received and being executed");
        if(players.containsKey(sender)) {
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

        if(selectedPlayer.isPresent()){
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

            if(selectedGamePlayer.isPresent()) {
                Player target = selectedGamePlayer.get();
                Game targetGame = (Game) playersToLobbiesAndGames.get(target);
                players.put(sender, target);

                System.out.println("[SERVER]: sending SetNicknameCommand and RestoreGameCommand to client " + sender);
                requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();
                requestToClient(sender, new RestoreGameCommand(targetGame.generateDTO((InGamePlayer) target))); //restoreGame();

                for (var player : targetGame.getPlayers())
                    if (player.isActive()) {
                        VirtualClient targetClient = keyReverseLookup(players, player::equals);
                        requestToClient(targetClient, new ToggleActiveCommand(nickname)); //toggleActive()
                    }

                ((InGamePlayer) target).toggleActive();
                //FIXME: restoreGame va chiamata anche quando non c'è il gioco ma il file salvato perchè il server
                // era crashato
                // inoltre serve uno stato awaitingReconnectionsState (potremmo usarlo come timeout?)
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
        System.out.println("[CLIENT]: keepAlive command received from " + sender);

        //TODO: update Timer on VirtualClient Timr (add attributes or methods for management)
        requestToClient(sender, new KeepAliveCommand());
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

    public void leaveLobby(VirtualClient sender) {
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

        System.out.println("[SERVER]: sending UpdateLobbiesCommand to clients");
        for(var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
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
                return;
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
                return;
            } catch(UnexpectedPlayerException e){
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Not this player's turn")
                        )
                ); //throwException();
                return;
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
                return;
            } catch (InvalidCardPositionException e){
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new InvalidCardPositionException("Provided coordinates are not valid for placing a card")
                        )
                ); //throwException();
                return;
            }
        else {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Provided card is not of a playable type")
                    )
            ); //throwException();
            return;
        }

        System.out.println("[SERVER]: sending PlaceCardCommand to clients");
        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            requestToClient(
                    targetClient,
                    new PlaceCardCommand(targetPlayer.getNickname(), coordinates, targetCard.ID, playedSide,
                            targetPlayer.getOwnedResources(), targetPlayer.getOpenCorners(), targetPlayer.getPoints()
                    )
            ); //placeCard();
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
            return;
        } catch (UnexpectedPlayerException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
            return;
        } catch (UnknownStringException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such deck exists")
                    )
            ); //throwException();
            return;
        } catch (EmptyDeckException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new EmptyDeckException("Selected deck is empty")
                    )
            ); //throwException();
            return;
        }

        System.out.println("[SERVER]: sending ReceiveCardCommand to client");
        requestToClient(sender, new ReceiveCardCommand(List.of(targetPlayer.getCardsInHand().getLast().ID)));


        System.out.println("[SERVER]: sending ReplaceCardCommand to clients");

        CardDeck<? extends Card> selectedDeck;
        if (deck.trim().equalsIgnoreCase("RESOURCE"))
            selectedDeck = targetGame.getResourceCardsDeck();
        else
            selectedDeck = targetGame.getGoldCardsDeck();

        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            requestToClient(targetClient, new ReplaceCardCommand(List.of(new Triplet<>(
                    targetGame.peekFrom(selectedDeck) == null ? -1 : targetGame.peekFrom(selectedDeck).ID, deck.toUpperCase()+"_DECK", -1)
            )));
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
            return;
        } catch (UnexpectedPlayerException e){
            requestToClient(sender,
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
            return;
        } catch (InvalidDeckPositionException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidDeckPositionException("Cannot understand which card to draw")
                    )
            ); //throwException();
            return;
        } catch (UnknownStringException e){
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such placed cards exist")
                    )
            ); //throwException();
            return;
        } catch (EmptyDeckException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new EmptyDeckException("No card in selected slot")
                    )
            ); //throwException();
            return;
        }

        System.out.println("[SERVER]: sending ReceiveCardCommand to clients");
        requestToClient(sender, new ReceiveCardCommand(List.of(targetPlayer.getCardsInHand().getLast().ID)));
        //receiveCard();

        //FIXME: ... (a sto punto faccio direttamente la getArray qua e lo passo nella funzione draw nello stato)
        ArrayList<Triplet<Integer, String, Integer>> newCard = new ArrayList<>();
        if (deck.trim().equalsIgnoreCase("RESOURCE"))
            newCard.add(new Triplet<>(targetGame.getPlacedResources()[position].ID, deck, position));
        else newCard.add(new Triplet<>(targetGame.getPlacedGolds()[position].ID, deck, position));

        System.out.println("[SERVER]: sending ReplaceCardCommand to clients");
        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            requestToClient(targetClient, new ReplaceCardCommand(newCard)); //replaceCard();
        }
    }

    public void leaveGame(VirtualClient sender) {
        System.out.println("[CLIENT]: LeaveGameCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        targetPlayer.toggleActive();
        players.remove(sender);

        long activePlayers = targetGame.getPlayers().stream()
                .filter(InGamePlayer::isActive)
                .count();

        System.out.println("[SERVER]: sending ToggleActiveCommand to clients");

        for(var player : targetGame.getPlayers())
            if(player.isActive()) {
                VirtualClient targetClient = keyReverseLookup(players, player::equals);
                requestToClient(targetClient, new ToggleActiveCommand(player.getNickname()));
                //toggleActive();
            }

        // TODO/FIXME: potremmo usare uno stato awaitingReconnectionsState (come timeout?) come per createPlayer()?
        if(activePlayers == 1){
            //TODO: sospensione gioco (+ notificare)
            //TODO: timeout per aspettare altre riconnessioni, se scade vince l'unico rimasto
            // if timeout scaduto:

        } else if(activePlayers == 0){
            for(var player: targetGame.getPlayers())
                playersToLobbiesAndGames.remove(player);
            lobbiesAndGames.remove(targetGame);
        }
    }

    public void broadcastMessage(VirtualClient sender, String message) {
        System.out.println("[CLIENT]: BroadcastMessageCommand received and being executed");
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

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