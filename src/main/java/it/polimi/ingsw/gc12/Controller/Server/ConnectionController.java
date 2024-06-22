package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;

import java.util.Optional;
import java.util.UUID;

/**
 * The {@code ConnectionController} class extends the {@link ServerController} and manages the initial
 * connection state of players, handling operations like setting nicknames, creating lobbies, and joining lobbies.
 * <p>
 * This class ensures synchronization and state management for players connecting to the server.
 */
public class ConnectionController extends ServerController {

    /**
     * The singleton instance of the {@code ConnectionController}.
     */
    private static final ConnectionController SINGLETON_INSTANCE = new ConnectionController();

    /**
     * Private constructor to prevent instantiation.
     */
    private ConnectionController() {}

    /**
     * Returns the singleton instance of the {@code ConnectionController}.
     *
     * @return the singleton instance
     */
    public static ConnectionController getInstance() {
        return SINGLETON_INSTANCE;
    }

    /**
     * Generates a new player with the given nickname, sends the relevant commands to the client,
     * and updates the server model with the new player.
     *
     * @param sender   the network session of the client
     * @param nickname the nickname for the new player
     */
    @Override
    protected void generatePlayer(NetworkSession sender, String nickname) {
        Player target = new Player(nickname);

        System.out.println("[SERVER]: sending SetNicknameCommand and SetLobbiesCommand to client " + sender);
        sender.getListener().notified(new SetNicknameCommand(nickname));

        MODEL.LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            sender.getListener().notified(new SetLobbiesCommand(MODEL.getLobbiesMap()));
        } finally {
            MODEL.LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }

        sender.setPlayer(target);
        putActivePlayer(sender, target);
        MODEL.addListener(sender.getListener());
    }

    /**
     * Sets the nickname for a player. If the nickname is already taken, an exception is thrown.
     *
     * @param sender   the network session of the client
     * @param nickname the new nickname for the player
     */
    @Override
    public void setNickname(NetworkSession sender, String nickname) {
        System.out.println("[CLIENT]: SetNicknameCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        ACTIVE_PLAYERS_LOCK.readLock().lock();
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
            } else {
                sender.getPlayer().setNickname(nickname);
                System.out.println("[SERVER]: sending SetNicknameCommand to client " + sender);
                sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();
            }
        } finally {
            ACTIVE_PLAYERS_LOCK.readLock().unlock();
        }
    }

    /**
     * Creates a new lobby with the specified maximum number of players.
     * If the number of players is out of the valid range, an exception is thrown.
     *
     * @param sender     the network session of the client
     * @param maxPlayers the maximum number of players for the lobby
     */
    @Override
    public void createLobby(NetworkSession sender, int maxPlayers) {
        System.out.println("[CLIENT]: CreateLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        if (maxPlayers < 2 || maxPlayers > 4) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid number of max players (out of range: accepted [2-4])")
                    )
            );
            return;
        }

        UUID lobbyUUID;
        LobbyController controller;

        MODEL.LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            do {
                lobbyUUID = UUID.randomUUID();
            } while (MODEL.getLobbyController(lobbyUUID) != null || MODEL.getGameController(lobbyUUID) != null);

            Lobby lobby = new Lobby(lobbyUUID, sender.getPlayer(), maxPlayers);
            controller = MODEL.createLobbyController(lobby);
        } finally {
            MODEL.LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
        sender.setController(controller);
    }

    /**
     * Adds a player to an existing lobby identified by the given UUID.
     * If the lobby is full or does not exist, an exception is thrown.
     *
     * @param sender    the network session of the client
     * @param lobbyUUID the UUID of the lobby to join
     */
    @Override
    public void joinLobby(NetworkSession sender, UUID lobbyUUID) {
        System.out.println("[CLIENT]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        try {
            MODEL.addPlayerToLobby(sender.getPlayer(), lobbyUUID);
        } catch (IllegalArgumentException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            );
            return;
        } catch (FullLobbyException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new FullLobbyException("Cannot join a full lobby")
                    )
            );
            return;
        }
        sender.setController(MODEL.getLobbyController(lobbyUUID));
    }
}
