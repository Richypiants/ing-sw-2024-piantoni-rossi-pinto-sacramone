package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.*;
import it.polimi.ingsw.gc12.Controller.KeepAliveCommand;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Controller.SetNicknameCommand;
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

    private boolean hasNoPlayer(VirtualClient client) throws Exception {
        if (!players.containsKey(client)) {
            client.requestToClient(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Unregistered client")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    //FIXME: abbastanza orribile e duplicato... playerState?
    private boolean inGame(VirtualClient client, boolean throwIf) throws Exception {
        if (players.get(client) instanceof InGamePlayer == throwIf) {
            client.requestToClient(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot execute action while " +
                                    (throwIf ? "" : "not ") + "in a game")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    private boolean inLobbyOrGame(VirtualClient client, boolean throwIf) throws Exception {
        if (playersToLobbiesAndGames.containsKey(players.get(client)) == throwIf) {
            client.requestToClient(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot execute action while " +
                                    (throwIf ? "" : "not ") + "in a lobby or in a game")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    private boolean validCard(VirtualClient sender, int cardID) throws Exception {
        if(!cardsList.containsKey(cardID)){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            ); //throwException();
            return false;
        }
        return true;
    }

    public void createPlayer(VirtualClient sender, String nickname) throws Exception {
        System.out.println("[SERVER]: CreatePlayerCommand received and being executed");
        if(players.containsKey(sender)) {
            sender.requestToClient(
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
            Player target = selectedPlayer.get();
            if((target instanceof InGamePlayer) && !((InGamePlayer) target).isActive()) {
                Game targetGame = (Game) playersToLobbiesAndGames.get(target);

                sender.requestToClient(new RestoreGameCommand(targetGame.generateDTO())); //restoreGame();

                for (var player : targetGame.getPlayers())
                    if (player.isActive()) {
                        VirtualClient targetClient = keyReverseLookup(players, player::equals);
                        targetClient.requestToClient(new ToggleActiveCommand(nickname)); //toggleActive()
                    }

                ((InGamePlayer) target).toggleActive();
                //FIXME: restoreGame va chiamata anche quando non c'è il gioco ma il file salvato perchè il server
                // era crashato
                // inoltre serve uno stato awaitingReconnectionsState (potremmo usarlo come timeout?)
            } else
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new IllegalArgumentException("Provided nickname is already taken")
                        )
                ); //throwException();
        } else {
            Player target = new Player(nickname);
            players.put(sender, target);
            System.out.println("[SERVER]: sending SetNicknameCommand and SetLobbiesCommand to client " + sender);
            sender.requestToClient(new SetNicknameCommand(nickname)); //setNickname();
            sender.requestToClient(
                    new SetLobbiesCommand(
                            lobbiesAndGames.entrySet().stream()
                                    .filter((entry) -> !(entry.getValue() instanceof Game))
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    )
            ); //setLobbies();
        }
    }

    public void setNickname(VirtualClient sender, String nickname) throws Exception {
        System.out.println("[SERVER]: SetNicknameCommand received and being executed");
        if (hasNoPlayer(sender) || inLobbyOrGame(sender, true)) return;

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if(selectedPlayer.isPresent()) {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            ); //throwException();
        } else {
            players.get(sender).setNickname(nickname);
            System.out.println("[SERVER]: sending SetNicknameCommand to client " + sender);
            sender.requestToClient(new SetNicknameCommand(nickname)); //setNickname();

            //TODO: update to other players too!
        }
    }

    public void keepAlive(VirtualClient sender) throws Exception {
        if(hasNoPlayer(sender)) return;

        //TODO: update Timer on VirtualClient Timr (add attributes or methods for management)
        sender.requestToClient(new KeepAliveCommand());
    }

    public void createLobby(VirtualClient sender, int maxPlayers) throws Exception {
        System.out.println("[SERVER]: CreateLobbyCommand received and being executed");
        if (hasNoPlayer(sender) || inLobbyOrGame(sender, true)) return;
        //TODO: si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        if(maxPlayers < 2 || maxPlayers > 4){
            sender.requestToClient(
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
                client.requestToClient(new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    public void joinLobby(VirtualClient sender, UUID lobbyUUID) throws Exception {
        System.out.println("[SERVER]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender) || inLobbyOrGame(sender, true)) return;

        if (!lobbiesAndGames.containsKey(lobbyUUID)) {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            ); //throwException();
            return;
        }

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);

        if(lobby instanceof Game){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("The provided UUID refers to a game and not to a lobby")
                    )
            ); //throwException();
            return;
        }

        if(lobby.getPlayersNumber() >= lobby.getMaxPlayers()){
            sender.requestToClient(
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

                targetClient.requestToClient(new StartGameCommand(lobbyUUID, lobby)); //startGame();

                //FIXME: should clients inform that they are ready before? (ready() method call?)
                //Calls to game creation, generateInitialCards ...
                newGame.getCurrentState().transition();
            }

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & GameLobby?
            while(lobby.getPlayersNumber() > 0) {
                lobby.removePlayer(lobby.getPlayers().getFirst());
            }
        }

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for(var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                client.requestToClient(new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    public void leaveLobby(VirtualClient sender) throws Exception {
        System.out.println("[SERVER]: LeaveLobbyCommand received and being executed");
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
                client.requestToClient(new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    public void pickObjective(VirtualClient sender, int cardID) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        if(!cardsList.containsKey(cardID)){
            sender.requestToClient(
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
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot pick an objective card in this state")
                        )
                ); //throwException();
                return;
            } catch (CardNotInHandException e){
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                ); //throwException();
            } catch (AlreadySetCardException e){
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new AlreadySetCardException("Secret objective already chosen")
                        )
                ); //throwException();
            }
        else {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Card with provided cardID is not of type ObjectiveCard")
                    )
            ); //throwException();
        }
    }

    public void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID,
                          Side playedSide) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false) || !validCard(sender, cardID)) return;

        if(Arrays.stream(Side.values()).noneMatch((side) -> side.equals(playedSide))){
            sender.requestToClient(
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
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot place a card in this state")
                        )
                ); //throwException();
                return;
            } catch(UnexpectedPlayerException e){
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Not this player's turn")
                        )
                ); //throwException();
                return;
            } catch (CardNotInHandException e){
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                ); //throwException();
            } catch (NotEnoughResourcesException e){
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new NotEnoughResourcesException(
                                        "Player doesn't own the required resources to play the provided card"
                                )
                        )
                ); //throwException();
                return;
            } catch (InvalidCardPositionException e){
                sender.requestToClient(
                        new ThrowExceptionCommand(
                                new InvalidCardPositionException("Provided coordinates are not valid for placing a card")
                        )
                ); //throwException();
                return;
            }
        else {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Provided card is not of a playable type")
                    )
            ); //throwException();
            return;
        }

        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            targetClient.requestToClient(
                    new PlaceCardCommand(targetPlayer.getNickname(), coordinates, targetCard.ID, playedSide,
                            targetPlayer.getOwnedResources(), targetPlayer.getOpenCorners(), targetPlayer.getPoints()
                    )
            ); //placeCard();
        }
    }

    public void drawFromDeck(VirtualClient sender, String deck) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        try{
            targetGame.getCurrentState().drawFrom(targetPlayer, deck);
        } catch(ForbiddenActionException e) {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a card from a deck in this state")
                    )
            ); //throwException();
            return;
        } catch (UnexpectedPlayerException e){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
            return;
        } catch (UnknownStringException e){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such deck exists")
                    )
            ); //throwException();
            return;
        } catch (EmptyDeckException e) {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new EmptyDeckException("Selected deck is empty")
                    )
            ); //throwException();
            return;
        }

        sender.requestToClient(new ReceiveCardCommand(List.of(targetPlayer.getCardsInHand().getLast().ID)));
        //receiveCard();
    }

    public void drawFromVisibleCards(VirtualClient sender, String deck, int position) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        try{
            targetGame.getCurrentState().drawFrom(targetPlayer, deck, position);
        } catch(ForbiddenActionException e) {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a visible card in this state")
                    )
            ); //throwException();
            return;
        } catch (UnexpectedPlayerException e){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
            return;
        } catch (InvalidDeckPositionException e){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new InvalidDeckPositionException("Cannot understand which card to draw")
                    )
            ); //throwException();
            return;
        } catch (UnknownStringException e){
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such placed cards exist")
                    )
            ); //throwException();
            return;
        } catch (EmptyDeckException e) {
            sender.requestToClient(
                    new ThrowExceptionCommand(
                            new EmptyDeckException("No card in selected slot")
                    )
            ); //throwException();
            return;
        }

        sender.requestToClient(new ReceiveCardCommand(List.of(targetPlayer.getCardsInHand().getLast().ID)));
        //receiveCard();

        //FIXME: ... (a sto punto faccio direttamente la getArray qua e lo passo nella funzione draw nello stato)
        ArrayList<Triplet<Integer, String, Integer>> newCard = new ArrayList<>();
        if (deck.trim().equalsIgnoreCase("RESOURCE"))
            newCard.add(new Triplet<>(targetGame.getPlacedResources()[position].ID, deck, position));
        else newCard.add(new Triplet<>(targetGame.getPlacedGolds()[position].ID, deck, position));


        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            targetClient.requestToClient(new ReplaceCardCommand(newCard)); //replaceCard();
        }
    }

    public void leaveGame(VirtualClient sender) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        targetPlayer.toggleActive();
        players.remove(sender);

        long activePlayers = targetGame.getPlayers().stream()
                .filter(InGamePlayer::isActive)
                .count();

        for(var player : targetGame.getPlayers())
            if(player.isActive()) {
                VirtualClient targetClient = keyReverseLookup(players, player::equals);
                targetClient.requestToClient(new ToggleActiveCommand(player.getNickname()));
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

    public void directMessage(VirtualClient sender, String receiverNickname, String message) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(receiverNickname))
                .findAny();

        if(selectedPlayer.isPresent()) {
            Player receiverPlayer = selectedPlayer.get();
            if(playersToLobbiesAndGames.get(players.get(sender)).equals(playersToLobbiesAndGames.get(receiverPlayer))) {
                if(((InGamePlayer) receiverPlayer).isActive())
                    keyReverseLookup(players, receiverPlayer::equals).requestToClient(
                            new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                    );
                else sender.requestToClient(
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Nickname provided has no active player associated in this game")
                        )
                );
            } else sender.requestToClient(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Nickname provided has no associated player in this game")
                    )
            );
        } else sender.requestToClient(
                new ThrowExceptionCommand(
                        new NotExistingPlayerException("Nickname provided has no associated player registered")
                )
        );
    }

    public void broadcastMessage(VirtualClient sender, String message) throws Exception {
        if (hasNoPlayer(sender) || inGame(sender, false)) return;

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

        for(var inGamePlayer : ((Game) playersToLobbiesAndGames.get(players.get(sender))).getPlayers())
            if(inGamePlayer.isActive())
                keyReverseLookup(players, inGamePlayer::equals).requestToClient(
                        new AddChatMessageCommand(senderPlayer.getNickname(), message, false)
                );
    }
}