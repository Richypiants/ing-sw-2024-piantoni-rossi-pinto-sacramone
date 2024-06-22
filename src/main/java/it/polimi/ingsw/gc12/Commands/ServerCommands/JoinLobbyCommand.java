package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

import java.util.UUID;

/**
 * Represents a server command for a client to join a lobby identified by its UUID.
 * Implements the {@link ServerCommand} interface.
 */
public class JoinLobbyCommand implements ServerCommand {

    private final UUID LOBBY_UUID;

    /**
     * Constructs a JoinLobbyCommand to join the lobby with the specified UUID.
     *
     * @param lobbyUUID The UUID of the lobby to join.
     */
    public JoinLobbyCommand(UUID lobbyUUID) {
        this.LOBBY_UUID = lobbyUUID;
    }

    /**
     * Executes the command by requesting to the server controller to join the lobby with the specified UUID.
     *
     * @param caller           The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.joinLobby(caller, LOBBY_UUID);
    }
}
