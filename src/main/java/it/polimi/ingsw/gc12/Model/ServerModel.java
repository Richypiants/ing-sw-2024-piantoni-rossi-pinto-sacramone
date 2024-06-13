package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.LobbyController;
import it.polimi.ingsw.gc12.Listeners.Listenable;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;
import it.polimi.ingsw.gc12.Utilities.JSONParser;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Represents the server-side model responsible for managing game and lobby controllers.
 * This class provides methods to create, access, and destroy controllers for lobbies and games.
 * It also maintains lists of listeners for lobby updates and ensures thread-safe operations
 * for adding, removing, and notifying these listeners.
 * <p>
 * The {@code ServerModel} class encapsulates functionality related to server-side game and lobby management.
 * It statically loads and maintains the lists of cards used in the game and provides methods to interact with lobby
 * and game controllers.
 * </p>
 * <p>
 * The class also implements the {@link Listenable} interface to allow for
 * registering, removing, and notifying listeners about operations made on the lobbies.
 * Additionally, it ensures thread safety when manipulating lists of listeners.
 * </p>
 */
public class ServerModel implements Listenable {

    /**
     * The map of cards used to perform server-side operations in the game.
     * Each card is mapped to its unique ID for easy access.
     */
    public static final Map<Integer, Card> CARDS_LIST = loadModelCards();

    /**
     * The map of cards used to graphically represent them on the clients.
     * Each client card is mapped to its unique ID for easy access.
     */
    public static final Map<Integer, ClientCard> CLIENTS_CARDS_LIST = loadClientCards();

    /**
     * The map of lobby controllers, indexed by lobby UUID.
     */
    private final Map<UUID, LobbyController> LOBBY_CONTROLLERS;

    /**
     * The lock used to ensure thread safety for lobby controllers.
     */
    public final ReentrantReadWriteLock LOBBY_CONTROLLERS_LOCK;

    /**
     * The map of game controllers, indexed by game UUID.
     */
    private final Map<UUID, GameController> GAME_CONTROLLERS;

    /**
     * The lock used to ensure thread safety for game controllers.
     */
    public final ReentrantReadWriteLock GAME_CONTROLLERS_LOCK;

    /**
     * The list of listeners for lobby updates.
     * This list allows thread-safe addition and removal of listeners.
     */
    public final CopyOnWriteArrayList<Listener> LOBBIES_LISTENERS;

    /**
     * Constructs a new instance of {@code ServerModel}.
     * Initializes the lobby and game controllers maps and the list of lobby listeners.
     */
    public ServerModel() {
        LOBBY_CONTROLLERS = new HashMap<>();
        LOBBY_CONTROLLERS_LOCK = new ReentrantReadWriteLock(true);
        GAME_CONTROLLERS = new HashMap<>();
        GAME_CONTROLLERS_LOCK = new ReentrantReadWriteLock(true);
        LOBBIES_LISTENERS = new CopyOnWriteArrayList<>();
    }

    /**
     * Loads the different classes constituting the model cards from JSON files.
     *
     * @return A map of card IDs to cards.
     */
    private static Map<Integer, Card> loadModelCards() {
        Map<Integer, Card> tmp = new HashMap<>();
        Objects.requireNonNull(
                JSONParser.deckFromJSONConstructor(
                        "/jsonFiles/resource_cards.json",
                        new TypeToken<ArrayList<ResourceCard>>() {
                        }
                )
        ).forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(
                JSONParser.deckFromJSONConstructor(
                        "/jsonFiles/gold_cards.json",
                        new TypeToken<ArrayList<GoldCard>>() {
                        }
                )
        ).forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(
                JSONParser.deckFromJSONConstructor(
                        "/jsonFiles/initial_cards.json",
                        new TypeToken<ArrayList<InitialCard>>() {
                        }
                )
        ).forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(
                JSONParser.deckFromJSONConstructor(
                        "/jsonFiles/objective_cards.json",
                        new TypeToken<ArrayList<ObjectiveCard>>() {
                        }
                )
        ).forEach((card) -> tmp.put(card.ID, card));

        return Collections.unmodifiableMap(tmp);
    }

    /**
     * Loads the client cards from JSON files.
     *
     * @return A map of client card IDs to client cards.
     */
    private static Map<Integer, ClientCard> loadClientCards() {
        return JSONParser.generateClientCardsFromJSON("/jsonFiles/client_cards.json")
                .stream().collect(Collectors.toMap((card) -> card.ID, (card) -> card));
    }

    /**
     * Retrieves the lobby controller associated with the specified lobby UUID.
     *
     * @param lobbyUUID The UUID of the lobby.
     * @return The lobby controller associated with the specified UUID, or {@code null} if not found.
     */
    public LobbyController getLobbyController(UUID lobbyUUID) {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            return LOBBY_CONTROLLERS.get(lobbyUUID);
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Retrieves the map of lobby UUIDs to their corresponding lobbies.
     *
     * @return A map of lobby UUIDs to lobbies.
     */
    public Map<UUID, Lobby> getLobbiesMap() {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            return LOBBY_CONTROLLERS.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, (entry) -> (entry.getValue()).CONTROLLED_LOBBY));
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Creates a new lobby controller for the specified lobby.
     * This method notifies all registered listeners about the creation of the associated lobby with am {@link UpdateLobbyCommand}.
     *
     * @param lobby The lobby for which to create the controller.
     * @return The created lobby controller.
     */
    public LobbyController createLobbyController(Lobby lobby) {
        LobbyController createdController = new LobbyController(lobby);
        LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            LOBBY_CONTROLLERS.put(lobby.getRoomUUID(), createdController);
            notifyListeners(new UpdateLobbyCommand(lobby));
            return createdController;
        } finally {
            LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Destroys the given lobby controller associated with its managed lobby.
     * It also removes all the players currently into the lobby.
     * This method notifies all registered listeners about the deletion of the associated lobby with am {@link UpdateLobbyCommand}.
     *
     * @param controller The lobby controller to destroy.
     */
    public void destroyLobbyController(LobbyController controller) {
        LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            Lobby lobby = controller.CONTROLLED_LOBBY;
            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & Lobby?
            while (lobby.getPlayersNumber() > 0)
                lobby.removePlayer(lobby.getPlayers().getFirst());

            LOBBY_CONTROLLERS.remove(lobby.getRoomUUID());
            notifyListeners(new UpdateLobbyCommand(lobby));
        } finally {
            LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Adds a player to the specified lobby.
     * This method notifies all registered listeners about the status change of the associated lobby with an {@link UpdateLobbyCommand}.
     *
     * @param target   The player to be added to the lobby.
     * @param lobbyUUID The UUID of the lobby.
     * @throws FullLobbyException if the lobby is full.
     */
    public void addPlayerToLobby(Player target, UUID lobbyUUID) throws FullLobbyException {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            LobbyController controller = LOBBY_CONTROLLERS.get(lobbyUUID);
            if (controller == null) throw new IllegalArgumentException();
            controller.CONTROLLED_LOBBY.addPlayer(target);
            notifyListeners(new UpdateLobbyCommand(controller.CONTROLLED_LOBBY));
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Removes a player from the specified lobby.
     * This method notifies all registered listeners about the status change of the associated lobby with an {@link UpdateLobbyCommand}.
     *
     * @param target The player to remove from the lobby.
     * @param lobby  The lobby from which to remove the player.
     */
    public void removePlayerFromLobby(Player target, Lobby lobby) {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            lobby.removePlayer(target);
            notifyListeners(new UpdateLobbyCommand(lobby));
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Retrieves the game controller associated with the specified game UUID.
     *
     * @param gameUUID The UUID of the game.
     * @return The game controller associated with the specified UUID, or {@code null} if not found.
     */
    public GameController getGameController(UUID gameUUID) {
        GAME_CONTROLLERS_LOCK.readLock().lock();
        try {
            return GAME_CONTROLLERS.get(gameUUID);
        } finally {
            GAME_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Creates a new game controller for the specified game.
     *
     * @param game The game for which to create the controller.
     * @return The created game controller.
     */
    public GameController createGameController(Game game) {
        GameController createdController = new GameController(game);
        GAME_CONTROLLERS_LOCK.writeLock().lock();
        try {
            GAME_CONTROLLERS.put(game.getRoomUUID(), createdController);
            return createdController;
        } finally {
            GAME_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Destroys the game controller associated with the specified game controller.
     *
     * @param controller The game controller to destroy.
     */
    public void destroyGameController(GameController controller) {
        GAME_CONTROLLERS_LOCK.writeLock().lock();
        try {
            GAME_CONTROLLERS.remove(controller.CONTROLLED_GAME.getRoomUUID());
        } finally {
            GAME_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Adds a listener to the list of the ones registered to lobbies updates, both inside or outside one.
     *
     * This method ensures thread-safe addition of listeners to the list.
     *
     * @param listener The listener to be added.
     */
    @Override
    public void addListener(Listener listener) {
        synchronized (LOBBIES_LISTENERS) {
            LOBBIES_LISTENERS.add(listener);
        }
    }

    /**
     * Removes a listener from the list of the ones registered to lobbies updates, both inside or outside one.
     *
     * This method ensures thread-safe removal of listeners from the list.
     *
     * @param listener The listener to be removed.
     */
    @Override
    public void removeListener(Listener listener) {
        synchronized (LOBBIES_LISTENERS) {
            LOBBIES_LISTENERS.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners with the specified command.
     *
     * This method ensures thread-safe iteration over the listeners list while notifying them.
     *
     * @param command The command to be sent to all listeners.
     */
    @Override
    public void notifyListeners(ClientCommand command) {
        synchronized (LOBBIES_LISTENERS) {
            for (var listener : LOBBIES_LISTENERS)
                listener.notified(command);
        }
    }
}
