package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command for a client to leave the current game.
 * Implements the {@link ServerCommand} interface.
 */
public class LeaveGameCommand implements ServerCommand {

    /**
     * Constructs a LeaveGameCommand.
     */
    public LeaveGameCommand() {
    }

    /**
     * Executes the command by requesting to the server controller to handle the client leaving the game.
     *
     * @param caller           The network session of the client initiating the command.
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.leaveGame(caller);
    }
}
