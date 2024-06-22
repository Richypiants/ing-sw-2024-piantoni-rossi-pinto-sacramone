package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command for a client to leave the current lobby.
 * Implements the {@link ServerCommand} interface.
 */
public class LeaveLobbyCommand implements ServerCommand {

    private final boolean IS_INACTIVE;

    /**
     * Constructs a LeaveLobbyCommand.
     *
     * @param isInactive Flag indicating whether the client is leaving due to inactivity.
     */
    public LeaveLobbyCommand(boolean isInactive) {
        this.IS_INACTIVE = isInactive;
    }

    /**
     * Executes the command by requesting to the server controller to handle the client leaving the lobby.
     *
     * @param caller           The network session of the client initiating the command.
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.leaveLobby(caller, IS_INACTIVE);
    }
}
