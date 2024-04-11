package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.Controller;
import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;
import static it.polimi.ingsw.gc12.Utilities.Commons.varargsToArrayList;

//FIXME: abstract with static methods or singleton? METTERE GLI OVERRIDE!
//TODO:  In every custom exception write in the constructor call a string verbosely describing the event that caused it and why

/*TODO: In case of high traffic volumes on network, we can reduce it by sending the updates to lobby states (creation, updates) only to clients
        which aren't already in a lobby. */
public abstract class ServerController extends Controller {

    public static final Map<Integer, Card> cardsList = loadCards();
    public static final Map<VirtualClient, Player> players = new HashMap<>();
    public static final Map<UUID, GameLobby> lobbiesAndGames = new HashMap<>();
    public static final Map<Player, GameLobby> playersToLobbiesAndGames = new HashMap<>();

    private static Map<Integer, Card> loadCards() {
        //TODO: map of maps?
        Map<Integer, Card> tmp = new HashMap<>();
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));

        return Collections.unmodifiableMap(tmp);
    }

    private static boolean hasNoPlayer(VirtualClient client) throws Throwable{
        if(players.containsKey(client)) {
            client.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new NotExistingPlayerException("Unregistered client")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    //FIXME: abbastanza orribile e duplicato... playerState?
    private static boolean inGame(VirtualClient client) throws Throwable{
        if(players.get(client) instanceof InGamePlayer) {
            client.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Cannot execute action while in a game")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    private static boolean inLobbyOrGame(VirtualClient client) throws Throwable{
        if(playersToLobbiesAndGames.containsKey(players.get(client))) {
            client.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Cannot execute action while in a lobby or in a game")
                    )
            ); //throwException();
            return true;
        }
        return false;
    }

    public static boolean validCard(VirtualClient sender, int cardID) throws Throwable {
        if(!cardsList.containsKey(cardID)){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            ); //throwException();
            return false;
        }
        return true;
    }

    public static void createPlayer(VirtualClient sender, String nickname) throws Throwable {
        if(players.containsKey(sender)) {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
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

                sender.serverMessage(varargsToArrayList("restoreGame", targetGame.generateDTO())); //restoreGame();

                for (var player : targetGame.getPlayers())
                    if (player.isActive()) {
                        VirtualClient targetClient = keyReverseLookup(players, player::equals);
                        targetClient.serverMessage(varargsToArrayList("toggleActive", nickname)); //toggleActive()
                    }

                ((InGamePlayer) target).toggleActive();
                //FIXME: restoreGame va chiamata anche quando non c'è il gioco ma il file salvato perchè il server
                // era crashato
                // inoltre serve uno stato awaitingReconnectionsState (potremmo usarlo come timeout?)
            } else
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new IllegalArgumentException("Provided nickname is already taken")
                        )
                ); //throwException();
        } else {
            Player target = new Player(nickname);
            players.put(sender, target);
            sender.serverMessage(
                    varargsToArrayList(
                            "setLobbies",
                            lobbiesAndGames.entrySet().stream()
                                    .filter((entry) -> !(entry.getValue() instanceof Game))
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    )
            ); //setLobbies();
        }
    }

    public static void setNickname(VirtualClient sender, String nickname) throws Throwable {
        if(hasNoPlayer(sender) || inLobbyOrGame(sender)) return;

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if(selectedPlayer.isPresent()) {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            ); //throwException();
        } else {
            players.get(sender).setNickname(nickname);

            //TODO: decide what to send back
            //sender.serverMessage(varargsToArrayList(...)); //...();
        }
    }

    public void keepAlive(VirtualClient sender) throws Throwable{
        if(hasNoPlayer(sender)) return;

        //TODO: keepAlive management...

    }

    public static void createLobby(VirtualClient sender, int maxPlayers) throws Throwable {
        if(hasNoPlayer(sender) || inLobbyOrGame(sender)) return;
        //TODO: si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        if(maxPlayers < 2 || maxPlayers > 4){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
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
        } while (!lobbiesAndGames.containsKey(lobbyUUID));

        lobbiesAndGames.put(lobbyUUID, lobby);
        playersToLobbiesAndGames.put(target, lobby);

        for(var client : players.keySet())
            if(!inGame(client))
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, lobby)); //updateLobby();
    }

    public static void joinLobby(VirtualClient sender, UUID lobbyUUID) throws Throwable {
        if(hasNoPlayer(sender) || inLobbyOrGame(sender)) return;

        if(lobbiesAndGames.containsKey(lobbyUUID)){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            ); //throwException();
            return;
        }

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);

        if(lobby instanceof Game){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("The provided UUID refers to a game and not a lobby")
                    )
            ); //throwException();
            return;
        }

        if(lobby.getPlayersNumber() >= lobby.getMaxPlayers()){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
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

                targetClient.serverMessage(varargsToArrayList("startGame", lobbyUUID, lobby)); //startGame();

                //FIXME: should clients inform that they are ready before? (ready() method call?)
                //Calls to game creation, generateInitialCards ...
                newGame.getCurrentState().transition();
            }

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & GameLobby?
            while(lobby.getPlayersNumber() > 0) {
                lobby.removePlayer(lobby.getPlayers().getFirst());
            }
        }

        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for(var client : players.keySet())
            if(!inGame(client))
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, lobby)); //updateLobby();
    }

    public static void leaveLobby(VirtualClient sender) throws Throwable {
        if(hasNoPlayer(sender) || inGame(sender) || !inLobbyOrGame(sender)) return;

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

        for(var client : players.keySet())
            if(!inGame(client))
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, lobby)); //updateLobby();
    }

    public static void pickObjective(VirtualClient sender, int cardID) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender)) return;

        if(!cardsList.containsKey(cardID)){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
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
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new ForbiddenActionException("Cannot pick an objective card in this state")
                        )
                ); //throwException();
                return;
            } catch (CardNotInHandException e){
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                ); //throwException();
            } catch (AlreadySetCardException e){
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new AlreadySetCardException("Secret objective already chosen")
                        )
                ); //throwException();
            }
        else {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new InvalidCardTypeException("Card with provided cardID is not of type ObjectiveCard")
                    )
            ); //throwException();
        }
    }

    public static void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID,
                                 Side playedSide) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender) || !validCard(sender, cardID)) return;

        if(Arrays.stream(Side.values()).noneMatch((side) -> side.equals(playedSide))){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
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
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new ForbiddenActionException("Cannot place a card in this state")
                        )
                ); //throwException();
                return;
            } catch(UnexpectedPlayerException e){
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new UnexpectedPlayerException("Not this player's turn")
                        )
                ); //throwException();
                return;
            } catch (CardNotInHandException e){
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                ); //throwException();
            } catch (NotEnoughResourcesException e){
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new NotEnoughResourcesException(
                                        "Player doesn't own the required resources to play the provided card"
                                )
                        )
                ); //throwException();
                return;
            } catch (InvalidCardPositionException e){
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new InvalidCardPositionException("Provided coordinates are not valid for placing a card")
                        )
                ); //throwException();
                return;
            }
        else {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new InvalidCardTypeException("Provided card is not of a playable type")
                    )
            ); //throwException();
            return;
        }

        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            targetClient.serverMessage(
                    varargsToArrayList(
                            "placeCard", targetPlayer.getNickname(), coordinates, targetCard.ID, playedSide,
                            targetPlayer.getOwnedResources(), targetPlayer.getOpenCorners(), targetPlayer.getPoints()
                    )
            ); //placeCard();
        }
    }

    public static void drawFromDeck(VirtualClient sender, String deck) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        try{
            targetGame.getCurrentState().drawFrom(targetPlayer, deck);
        } catch(ForbiddenActionException e) {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Cannot draw a card from a deck in this state")
                    )
            ); //throwException();
            return;
        } catch (UnexpectedPlayerException e){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
            return;
        } catch (UnknownStringException e){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new UnknownStringException("No such deck exists")
                    )
            ); //throwException();
            return;
        }

        sender.serverMessage(varargsToArrayList("receiveCard", targetPlayer.getCardsInHand().getLast().ID));
        //receiveCard();
    }

    public static void drawFromVisibleCards(VirtualClient sender, String deck, int position) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = (Game) playersToLobbiesAndGames.get(targetPlayer);

        try{
            targetGame.getCurrentState().drawFrom(targetPlayer, deck, position);
        } catch(ForbiddenActionException e) {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Cannot draw a visible card in this state")
                    )
            ); //throwException();
            return;
        } catch (UnexpectedPlayerException e){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            ); //throwException();
            return;
        } catch (InvalidDeckPositionException e){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new InvalidDeckPositionException("Cannot understand which card to draw")
                    )
            ); //throwException();
            return;
        } catch (UnknownStringException e){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new UnknownStringException("No such placed cards exist")
                    )
            ); //throwException();
            return;
        }

        sender.serverMessage(varargsToArrayList("receiveCard", targetPlayer.getCardsInHand().getLast().ID));
        //receiveCard();

        //FIXME: ... (a sto punto faccio direttamente la getArray qua e lo passo nella funzione draw nello stato)
        Card newCard;
        if (deck.trim().equalsIgnoreCase("RESOURCE"))
            newCard = targetGame.getPlacedResources()[position];
        else newCard = targetGame.getPlacedGold()[position];

        for(var player : targetGame.getPlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            targetClient.serverMessage(varargsToArrayList("replaceCard", newCard.ID, deck, position));
            //replaceCard();
        }
    }

    public static void leaveGame(VirtualClient sender) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender)) return;

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
                targetClient.serverMessage(varargsToArrayList("toggleActive", player.getNickname()));
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

    public static void directMessage(VirtualClient sender, String receiverNickname, String message) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender)) return;

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(receiverNickname))
                .findAny();

        if(selectedPlayer.isPresent()) {
            Player receiverPlayer = selectedPlayer.get();
            if(playersToLobbiesAndGames.get(players.get(sender)).equals(playersToLobbiesAndGames.get(receiverPlayer))) {
                if(((InGamePlayer) receiverPlayer).isActive())
                    keyReverseLookup(players, receiverPlayer::equals).serverMessage(
                            varargsToArrayList(
                                    "addChatMessage",
                                    message
                            )
                    );
                else sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new UnexpectedPlayerException("Nickname provided has no active player associated in this game")
                        )
                );
            } else sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new NotExistingPlayerException("Nickname provided has no associated player in this game")
                    )
            );
        } else sender.serverMessage(
                varargsToArrayList(
                        "throwException",
                        new NotExistingPlayerException("Nickname provided has no associated player registered")
                )
        );
    }

    public static void broadcastMessage(VirtualClient sender, String message) throws Throwable {
        if(hasNoPlayer(sender) || !inGame(sender)) return;

        for(var inGamePlayer : ((Game) playersToLobbiesAndGames.get(players.get(sender))).getPlayers())
            if(inGamePlayer.isActive())
                keyReverseLookup(players, inGamePlayer::equals).serverMessage(
                        varargsToArrayList(
                                "addChatMessage",
                                message
                        )
                );
    }
}