package it.polimi.ingsw.gc12.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

//FIXME: abstract with static methods or singleton? METTERE GLI OVERRIDE!
//TODO:  In every custom exception write in the constructor call a string verbosely describing the event that caused it and why

/*TODO: In case of high traffic volumes on network, we can reduce it by sending the updates to lobby states (creation, updates) only to clients
        which aren't already in a lobby too. */
public abstract class Controller {

    //TODO: caricare le carte
    public static final Map<Integer, Card> cardsList = loadCards();
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup(); //FIXME: why does publicLookup() not work?
    public static final Map<String, MethodHandle> commandHandles = createHandles();
    //FIXME: maybe create interface VirtualClient to uniform Sockets and RMI better and define Message methods
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

    //TODO: keep lambda or not?
    private static Map<String, MethodHandle> createHandles() {
        return Arrays.stream(Controller.class.getDeclaredMethods())
                .filter((method) -> Modifier.isPublic(method.getModifiers()))
                .map((method) -> {
                            try {
                                return new GenericPair<>(method.getName(), lookup.unreflect(method));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        /*MethodType type = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                        MethodHandle thisMethod = lookup.findVirtual(GameCommand.class, method.getName(), type);
                        */
                )
                .collect(Collectors.toUnmodifiableMap(GenericPair::getX, GenericPair::getY)
                );
    }

    private static ArrayList<Object> varargsToArrayList(Object... args){
        return new ArrayList<>(Arrays.asList(args));
    }

    private static boolean hasPlayer(VirtualClient client) throws Throwable{
        if(!players.containsKey(client)) {
            client.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new NotExistingPlayerException("Unregistered client")
                    )
            ); //throw();
            return false;
        }
        return true;
    }

    //FIXME: abbastanza orribile e duplicato... playerState?
    private static boolean inGame(VirtualClient client) throws Throwable{
        if(players.get(client) instanceof InGamePlayer) {
            client.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Cannot execute action while in a game")
                    )
            ); //throw();
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
            ); //throw();
            return true;
        }
        return false;
    }


    private boolean inGame(Player target){
        return playersToLobbiesAndGames.containsKey(target) && playersToLobbiesAndGames.get(target) instanceof Game;
    }

    public static void createPlayer(VirtualClient sender, String nickname) throws Throwable {
        if(players.containsKey(sender)) {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Client already registered")
                    )
            ); //throw();
            return;
        }

        Optional<Map.Entry<VirtualClient, Player>> selectedPlayer = players.entrySet().stream()
                .filter((entry) -> entry.getValue().getNickname().equals(nickname))
                .findAny();

        if(selectedPlayer.isPresent()){
            Player target = selectedPlayer.get().getValue();
            if((target instanceof InGamePlayer) && !((InGamePlayer) target).isActive())
                sender.serverMessage(functionName, gameInfos); //TODO: restoreGame();
            else
                sender.serverMessage(
                        varargsToArrayList(
                                "throwException",
                                new IllegalArgumentException("Provided nickname is already taken")
                        )
                ); //throw();
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
        if(!hasPlayer(sender) || inGame(sender)) return;

        Optional<Map.Entry<VirtualClient, Player>> selectedPlayer = players.entrySet().stream()
                .filter((entry) -> entry.getValue().getNickname().equals(nickname))
                .findAny();

        if(selectedPlayer.isPresent()) {
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            ); //throw();
            return;
        }

        players.get(sender).setNickname(nickname);

        //TODO: decide what to send back
        sender.serverMessage(varargsToArrayList(...));
    }

    public void keepAlive(VirtualClient sender) throws Throwable{
        if(!hasPlayer(sender)) return;

        //TODO: keepAlive management...

    }

    public static void createLobby(VirtualClient sender, int maxPlayers) throws Throwable {
        if(!hasPlayer(sender) || inLobbyOrGame(sender)) return;
        //TODO: si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        if(maxPlayers < 2 || maxPlayers > 4){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("Invalid number of max players (out of range: accepted [2-4])")
                    )
            );
            return;
        }

        //FIXME: fixare ordine parametri GameLobby in UML
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
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, lobby.generateDTO()));
    }

    public static void joinLobby(VirtualClient sender, UUID lobbyUUID) throws Throwable {
        if(!hasPlayer(sender) || inLobbyOrGame(sender)) return;

        if(lobbiesAndGames.containsKey(lobbyUUID)){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("No lobby with provided UUID")
                    )
            );
            return;
        }

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);

        if(lobby instanceof Game){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new IllegalArgumentException("Provided UUID refers to a game and not a lobby")
                    )
            );
            return;
        }

        if(lobby.getPlayersNumber() >= lobby.getMaxPlayers()){
            sender.serverMessage(
                    varargsToArrayList(
                            "throwException",
                            new ForbiddenActionException("Cannot join a full lobby")
                    )
            );
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
                VirtualClient targetClient = players.entrySet().stream()
                        .filter((entry) -> entry.getValue().equals(player))
                        .findFirst()
                        .get()
                        .getKey();
                InGamePlayer targetInGamePlayer = newGame.getPlayers().stream()
                        .filter((inGamePlayer) -> inGamePlayer.getNickname().equals(player.getNickname()))
                        .findFirst()
                        .get();

                players.put(
                        targetClient,
                        targetInGamePlayer
                );

                playersToLobbiesAndGames.remove(player);
                playersToLobbiesAndGames.put(targetInGamePlayer, newGame);

                targetClient.serverMessage(startGame, gameInfos);
            }

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & GameLobby?
            while(lobby.getPlayersNumber() > 0) {
                lobby.removePlayer(lobby.getPlayers().getFirst());
            }
        }

        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for(var client : players.keySet())
            if(!inGame(client))
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, lobby.generateDTO()));
    }

    public static void leaveLobby(VirtualClient sender) throws Throwable {
        if(!hasPlayer(sender) || inGame(sender) || !inLobbyOrGame(sender)) return;
        //TODO: exceptions? not in lobby, ...

        Player target = players.get(sender);

        GameLobby lobby = playersToLobbiesAndGames.get(target);
        UUID lobbyUUID = lobbiesAndGames.entrySet().stream()
                .filter((entry) -> entry.getValue().equals(lobby))
                .findFirst()
                .flatMap(uuidGameLobbyEntry -> Optional.of(uuidGameLobbyEntry.getKey()))
                .orElseThrow(IllegalStateException::new);
        //Assuming that lobby is contained (thus maps are coherent): check with synchronization that this
        // invariant holds

        lobby.removePlayer(target);
        playersToLobbiesAndGames.remove(target);

        if (lobby.getPlayers().isEmpty()) {
            lobbiesAndGames.remove(lobbyUUID);
        }

        for(var client : players.keySet())
            if(!inGame(client))
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, lobby.generateDTO()));
    }

    public static void placeInitialCard(VirtualClient sender, Side side) throws Throwable {
        if(!hasPlayer(client)) return;
    //TODO: Continue working from this point

    public static void placeInitialCard(InGamePlayer player, Side side) throws ForbiddenActionException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .placeInitialCard(player, side);
    }

    public static void pickObjective(VirtualClient sender, int cardID) throws Throwable {
        if(!hasPlayer(client)) return;
        Card chosenCard = cardsList.get(cardID);

        if(chosenCard instanceof ObjectiveCard)
            playersToLobbiesAndGames.get(player).getCurrentState()
                    .pickObjective(player, (ObjectiveCard) chosenCard);
        else
            throw new InvalidCardTypeException();
    }

    public static void placeCard(VirtualClient sender, GenericPair<Integer, Integer> pair, int cardID, Side side) throws Throwable {
        if(!hasPlayer(client)) return;
        Card chosenCard = cardsList.get(cardID);

        if(chosenCard instanceof PlayableCard)
            playersToLobbiesAndGames.get(player).getCurrentState()
                    .placeCard(player, pair, (PlayableCard) chosenCard, side);
        else
            throw new InvalidCardTypeException();

    }

    public static void drawFromDeck(VirtualClient sender, String deck) throws Throwable {
        if(!hasPlayer(client)) return;
        playersToLobbiesAndGames.get(player).getCurrentState()
                .drawFrom(player, deck);
    }

    public static void drawFromVisibleCards(VirtualClient sender, String deck, int position) throws Throwable {
        if(!hasPlayer(client)) return;
        playersToLobbiesAndGames.get(player).getCurrentState()
                .drawFrom(player, deck, position);
    }

    public static void leaveGame(VirtualClient sender) throws Throwable {
        if(!hasPlayer(client)) return;
        //TODO: exceptions? not in game, ...

        Game game = (Game) playersToLobbiesAndGames.get(inGamePlayer);

        //TODO: gestire se rimangono 1 o meno giocatori
        /*if(lobby.getListOfPlayers().size() == 1){
            lobbyAndGameMap.remove(lobbyAndGameMap.entrySet().get(game));
            return;
        }*/

        //game.removePlayer(inGamePlayer); TODO: implementare per disconnessione utenti
        playersToLobbiesAndGames.remove(inGamePlayer);
    }

    public static void directMessage(VirtualClient sender, String receiverNickname, String message) throws Throwable {
        if(!hasPlayer(sender)) return;

    }

    public static void broadcastMessage(VirtualClient sender, String message) throws Throwable {
        if(!hasPlayer(sender)) return;


    }
}
