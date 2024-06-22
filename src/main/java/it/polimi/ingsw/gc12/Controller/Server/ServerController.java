package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Commands.KeepAliveCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

/**
 * The {@code ServerController} class is an abstract base class that implements the
 * {@link ServerControllerInterface} and provides common functionality for various types
 * of server controllers in the application.
 * <p>
 * This class manages network sessions and handles operations such as player management,
 * keep-alive routines, and command execution. Unimplemented method calls on the wrong controller
 * will fall back to this class's implementations.
 * <p>
 * Existing controller implementations include:
 * <ul>
 *     <li>{@link ConnectionController}</li>
 *     <li>{@link LobbyController}</li>
 *     <li>{@link GameController}</li>
 * </ul>
 */
public abstract class ServerController implements ServerControllerInterface {

    /**
     * The model representing the server's state.
     */
    public static final ServerModel MODEL = new ServerModel();
    /**
     * A map of inactive network sessions.
     * Assumes the nickname of a player is unique.
     */
    public static final ConcurrentHashMap<String, NetworkSession> INACTIVE_SESSIONS = new ConcurrentHashMap<>();
    /**
     * A map of active players associated with their network sessions.
     */
    protected static final Map<NetworkSession, Player> ACTIVE_PLAYERS = new HashMap<>();
    /**
     * A lock for synchronizing access to the active players map.
     */
    protected static final ReentrantReadWriteLock ACTIVE_PLAYERS_LOCK = new ReentrantReadWriteLock();

    /**
     * Adds a player to the active players map.
     * This method is thread-safe.
     *
     * @param session the network session associated with the player
     * @param player  the player to be added
     */
    public void putActivePlayer(NetworkSession session, Player player) {
        ACTIVE_PLAYERS_LOCK.writeLock().lock();
        try {
            ACTIVE_PLAYERS.put(session, player);
        } finally {
            ACTIVE_PLAYERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Retrieves the network session associated with a given player.
     * This method is thread-safe.
     *
     * @param player the player whose session is to be retrieved
     * @return the network session associated with the player
     */
    public NetworkSession getSessionFromActivePlayer(Player player) {
        ACTIVE_PLAYERS_LOCK.readLock().lock();
        try {
            return keyReverseLookup(ServerController.ACTIVE_PLAYERS, player::equals);
        } finally {
            ACTIVE_PLAYERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Removes a player from the active players map.
     * This method is thread-safe.
     *
     * @param session the network session associated with the player
     */
    protected void removeActivePlayer(NetworkSession session) {
        ACTIVE_PLAYERS_LOCK.writeLock().lock();
        try {
            ACTIVE_PLAYERS.remove(session);
        } finally {
            ACTIVE_PLAYERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Checks if a client has no associated player.
     * This can happen due to malicious interaction or connection errors.
     *
     * @param client the network session of the client
     * @return true if the client has no associated player, false otherwise
     */
    protected boolean hasNoPlayer(NetworkSession client) {
        if (client.getPlayer() == null) {
            client.getListener().notified(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Unregistered client")
                    )
            );
            return true;
        }
        return false;
    }

    /**
     * Renews the timeout timer task for a network session,
     * scheduling it for execution after some time or when a certain condition is satisfied.
     *
     * @param target the network session to renew the timeout task for
     */
    public void renewTimeoutTimerTask(NetworkSession target) {
        TimerTask timeoutTask = createTimeoutTask(target);
        target.scheduleTimeoutTimerTask(timeoutTask);
    }

    /**
     * Creates a timeout task for a network session.
     * The run method handles disconnections and ensures the thread doesn't execute further.
     *
     * @param target the network session to create the timeout task for
     * @return the created timeout task
     */
    protected TimerTask createTimeoutTask(NetworkSession target) {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println("[SERVER]" + target + " didn't send any keepAlive in 15"
                        + " seconds or the game has sent an update and its state is inconsistent, disconnecting...");
                ControllerInterface thisController = target.getController();
                if (thisController instanceof GameController)
                    ((GameController) thisController).leaveGame(target);
                else if (thisController instanceof LobbyController)
                    ((LobbyController) thisController).leaveLobby(target, true);
                else {
                    removeActivePlayer(target);
                    MODEL.removeListener(target.getListener());
                }

                cancel();
            }
        };
    }

    /**
     * Handles keep-alive commands from clients, replying with an acknowledgment.
     *
     * @param sender the network session sending the keep-alive command
     */
    public void keepAlive(NetworkSession sender) {
        System.out.println("[CLIENT]: keepAlive command received from " + sender + ". Resetting timeout");
        sender.getTimeoutTask().cancel();
        renewTimeoutTimerTask(sender);

        if (!(sender.getPlayer() instanceof InGamePlayer targetPlayer) || targetPlayer.isActive())
            sender.getListener().notified(new KeepAliveCommand());
    }

    /**
     * Generates a player for a network session.
     * Controller-dependent details must be implemented by overriding this method.
     *
     * @param sender   the network session
     * @param nickname the player's nickname
     */
    protected void generatePlayer(NetworkSession sender, String nickname) {
    }

    /**
     * Creates a new player and associates it with a network session.
     * This method is thread-safe.
     *
     * @param sender   the network session
     * @param nickname the nickname of the player to be created
     */
    public void createPlayer(NetworkSession sender, String nickname) {
        if (sender.getPlayer() != null) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Client already registered")
                    )
            );
            return;
        }

        ACTIVE_PLAYERS_LOCK.writeLock().lock();
        try {
            Optional<Player> selectedPlayer = ACTIVE_PLAYERS.values().stream()
                    .filter((player) -> player.getNickname().equals(nickname))
                    .findAny();

            if (selectedPlayer.isPresent()) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new IllegalArgumentException("Provided nickname is already taken")
                        )
                );
                return;
            }

            //Creating the timeoutRoutine that will be started in case the client doesn't send a keepAliveCommand in the 30 seconds span.
            renewTimeoutTimerTask(sender);

            System.out.println("[CLIENT]: CreatePlayerCommand received and being executed");
            NetworkSession target = INACTIVE_SESSIONS.get(nickname);
            if (target != null) {
                INACTIVE_SESSIONS.remove(nickname);
                sender.setController(target.getController());
            }

            ((ServerController) sender.getController()).generatePlayer(sender, nickname);
        } finally {
            ACTIVE_PLAYERS_LOCK.writeLock().unlock();
        }
    }

    /**
     * Changes the nickname for a player.
     *
     * @param sender   the network session of the player
     * @param nickname the new nickname
     */
    public void setNickname(NetworkSession sender, String nickname) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    /**
     * Creates a new lobby.
     *
     * @param sender     the network session of the player creating the lobby
     * @param maxPlayers the maximum number of players in the lobby
     */
    public void createLobby(NetworkSession sender, int maxPlayers) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    /**
     * Joins an existing lobby.
     *
     * @param sender   the network session of the player joining the lobby
     * @param lobbyUUID the unique identifier of the lobby
     */
    public void joinLobby(NetworkSession sender, UUID lobbyUUID) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    /**
     * Picks a color for a player.
     *
     * @param sender the network session of the player
     * @param color  the color to be picked
     */
    public void pickColor(NetworkSession sender, Color color) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a lobby!"))
        );
    }

    /**
     * Leaves a lobby.
     *
     * @param sender     the network session of the player leaving the lobby
     * @param isInactive flag indicating if the player is inactive
     */
    public void leaveLobby(NetworkSession sender, boolean isInactive) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a lobby!"))
        );
    }

    /**
     * Places a card on the game board.
     *
     * @param sender    the network session of the player
     * @param coordinates the coordinates where the card is to be placed
     * @param cardID    the unique identifier of the card
     * @param playedSide the side on which the card is to be placed
     */
    public void placeCard(NetworkSession sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    /**
     * Picks an objective card.
     *
     * @param sender the network session of the player
     * @param cardID the unique identifier of the card
     */
    public void pickObjective(NetworkSession sender, int cardID) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    /**
     * Draws a card from a deck.
     *
     * @param sender the network session of the player
     * @param deck   the name of the deck
     */
    public void drawFromDeck(NetworkSession sender, String deck) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    /**
     * Draws a card from visible cards.
     *
     * @param sender   the network session of the player
     * @param deck     the name of the deck
     * @param position the position of the card in the visible cards
     */
    public void drawFromVisibleCards(NetworkSession sender, String deck, int position) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    /**
     * Leaves a game.
     *
     * @param sender the network session of the player
     */
    public void leaveGame(NetworkSession sender) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    /**
     * Broadcasts a message to all players in a game.
     *
     * @param sender  the network session of the player
     * @param message the message to be sent to all
     */
    public void broadcastMessage(NetworkSession sender, String message) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    /**
     * Sends a direct message to a specific player.
     *
     * @param sender          the network session of the player
     * @param receiverNickname the nickname of the receiver
     * @param message         the message to be sent
     */
    public void directMessage(NetworkSession sender, String receiverNickname, String message) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }
}