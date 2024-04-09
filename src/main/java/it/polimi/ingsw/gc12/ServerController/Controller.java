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
public abstract class Controller {

    //TODO: caricare le carte
    public static final Map<Integer, Card> cardsList = loadCards();
    public static final MethodHandles.Lookup lookup = MethodHandles.lookup(); //FIXME: why does publicLookup() not work?
    public static final Map<String, MethodHandle> commandHandles = createHandles();
    //FIXME: maybe create interface VirtualClient to uniform Sockets and RMI better and define Message methods
    public static final Map<VirtualClient, Player> players = new HashMap<>();
    public static final Map<UUID, GameLobby> lobbiesAndGames = new HashMap<>();
    public static final Map<Player, GameLobby> playersToLobbiesAndGames = new HashMap<>();

    private static Map<Integer, Card> loadCards() {
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

    protected static Player getPlayerFromVirtualClient(VirtualClient client) {
        return Controller.players.get(client);
    }

    /*    private static void sendLobbies(Player target) {
            players.entrySet().stream()
                    .filter((entry) -> entry.getValue().equals(target))
                    .findAny().orElseThrow(NotExistingPlayerException::new).getKey()
                    .getServerMessage(functionName, arraylistOfLobbies);
        }

        public static void createPlayer(Player target, VirtualClient client, String nickname) {
            if(target != null || players.values().stream().anyMatch((player) -> player.getNickname().equals(nickname)))
                throw new AlreadyExistingPlayerException();

            target = new Player(nickname);
            players.put(client, target);

            sendLobbies(target);

        }
    */
    public static void setNickname(Player target, String nickname) {
        if (playersToLobbiesAndGames.containsValue(nickname))
            //throw new AlreadyExistingPlayerException();

            target.setNickname(nickname);
    }

    //Si può fare in ogni stato: gestire che non possa essere il primo messaggio
    public void keepAlive(Player player) {

    }

    public static void createLobby(Player player, int maxPlayers) {
        //TODO: exceptions? invalidMaxPlayers, ...
        // e tutte le varie eccezioni se il player è in Game e non in lobby?
        // si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        //FIXME: fixare anche il metodo del model nell'UML oltre a invertire ordine parametri in GameLobby
        //GameLobby lobby = new GameLobby(player, maxPlayers);
        UUID lobbyUUID;

        do {
            lobbyUUID = UUID.randomUUID();
        } while (!lobbiesAndGames.containsKey(lobbyUUID));

        //lobbiesAndGames.put(lobby.UUID, lobby);
        //playersToLobbiesAndGames.put(player, lobby);
    }

    public static void joinLobby(Player player, UUID lobbyUUID) {
        //TODO: exceptions? lobby non trovata, lobby piena, ...

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);
        lobby.addPlayer(player);
        playersToLobbiesAndGames.put(player, lobby);
    }

    public static void leaveLobby(Player player) {
        //TODO: exceptions? not in lobby, ...

        GameLobby lobby = playersToLobbiesAndGames.get(player);

        if (lobby.getListOfPlayers().size() == 1) {
            //lobbiesAndGames.remove(lobbiesAndGames.entrySet().get(lobby));
            return;
        }

        lobby.removePlayer(player);
        playersToLobbiesAndGames.remove(player);
    }

    public static void placeInitialCard(VirtualClient sender, Side side) throws Throwable {
        if(!hasPlayer(client)) return;
    //TODO: Continue working from this point

    public static void placeInitialCard(InGamePlayer player, Side side) throws ForbiddenActionException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .placeInitialCard(player, side);
    }

    public static void pickObjective(InGamePlayer player, int cardID) throws ForbiddenActionException, AlreadySetCardException, InvalidCardTypeException {
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

    public static void drawFromDeck(InGamePlayer player, String deck) throws UnexpectedPlayerException, ForbiddenActionException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .drawFrom(player, deck);
    }

    public static void drawFromVisibleCards(VirtualClient sender, String deck, int position) throws Throwable {
        if(!hasPlayer(client)) return;
        playersToLobbiesAndGames.get(player).getCurrentState()
                .drawFrom(player, deck, position);
    }

    public static void leaveGame(InGamePlayer inGamePlayer) {
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

    public static void directMessage(InGamePlayer sender, InGamePlayer receiver, String message) {

    }

    public static void broadcastMessage(InGamePlayer sender, String message) {

    }
}
