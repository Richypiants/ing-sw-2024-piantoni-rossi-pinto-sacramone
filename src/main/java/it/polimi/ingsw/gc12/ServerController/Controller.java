package it.polimi.ingsw.gc12.ServerController;

import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//FIXME: abstract with static methods or singleton? METTERE GLI OVERRIDE!
public class Controller {

    private static final Controller SINGLETON_CONTROLLER = new Controller();

    //TODO: caricare le carte
    public static final Map<Integer, Card> cardsList = null;
    public static final MethodHandles.Lookup lookup = MethodHandles.lookup(); //FIXME: why does publicLookup() not work?
    public static final Map<String, MethodHandle> commandHandles = createHandles();
    public static final Map<AsynchronousSocketChannel, Player> players = new HashMap<>();
    public static final Map<UUID, GameLobby> lobbiesAndGames = new HashMap<>();
    public static final Map<Player, GameLobby> playersToLobbiesAndGames = new HashMap<>();

    private Controller() {
    }

    public static Controller getInstance() {
        return SINGLETON_CONTROLLER;
    }

    //TODO: keep lambda or not?
    private static Map<String, MethodHandle> createHandles() {
        return Arrays.stream(Controller.class.getDeclaredMethods())
                .filter((method) -> Modifier.isPublic(method.getModifiers()) &&
                        !method.getName().equals("getInstance"))
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

    public void createPlayer(String nickname) {
        //FIXME: null is bad, maybe we should have a map<nicknames, players>?
        if (playersToLobbiesAndGames.containsValue(nickname)) //non va bene così...
            //throw new AlreadyExistingPlayerException();

            playersToLobbiesAndGames.put(new Player(nickname), null);
    }

    public void setNickname(Player target, String nickname) {
        if (playersToLobbiesAndGames.containsValue(nickname))
            //throw new AlreadyExistingPlayerException();

            target.setNickname(nickname);
    }

    //Si può fare in ogni stato: gestire che non possa essere il primo messaggio
    public void keepAlive(Player player) {

    }

    public void createLobby(Player player, int maxPlayers) {
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

    public void joinLobby(Player player, UUID lobbyUUID) {
        //TODO: exceptions? lobby non trovata, lobby piena, ...

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);
        lobby.addPlayer(player);
        playersToLobbiesAndGames.put(player, lobby);
    }

    public void leaveLobby(Player player) {
        //TODO: exceptions? not in lobby, ...

        GameLobby lobby = playersToLobbiesAndGames.get(player);

        if (lobby.getListOfPlayers().size() == 1) {
            //lobbiesAndGames.remove(lobbiesAndGames.entrySet().get(lobby));
            return;
        }

        lobby.removePlayer(player);
        playersToLobbiesAndGames.remove(player);
    }

    public void placeInitialCard(InGamePlayer player, Side side) throws ForbiddenActionException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .placeInitialCard(player, side);
    }

    public void pickObjective(InGamePlayer player, ObjectiveCard card) throws ForbiddenActionException, AlreadySetCardException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .pickObjective(player, card);
    }

    public void placeCard(InGamePlayer player, GenericPair<Integer, Integer> pair, PlayableCard card,
                          Side side) throws UnexpectedPlayerException, ForbiddenActionException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .placeCard(player, pair, card, side);
    }

    public void drawFromDeck(InGamePlayer player, String deck) throws UnexpectedPlayerException, ForbiddenActionException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .drawFrom(player, deck);
    }

    public void drawFromVisibleCards(InGamePlayer player, String deck, int position) throws UnexpectedPlayerException, ForbiddenActionException, InvalidPositionException, UnknownStringException {
        playersToLobbiesAndGames.get(player).getCurrentState()
                .drawFrom(player, deck, position);
    }

    public void leaveGame(InGamePlayer inGamePlayer) {
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

    public void directMessage(InGamePlayer sender, InGamePlayer receiver, String message) {

    }

    public void broadcastMessage(InGamePlayer sender, String message) {

    }
}
