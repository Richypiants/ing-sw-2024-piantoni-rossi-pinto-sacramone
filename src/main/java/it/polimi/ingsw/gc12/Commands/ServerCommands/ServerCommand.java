package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Commands.Command;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a command to be executed on the server side in response to client requests.
 * Extends the {@link Command} interface.
 */
public interface ServerCommand extends Command {

    /**
     * Executes the server command requested by the caller using the provided server controller.
     *
     * @param caller     The network session representing the client that requested the command.
     * @param controller The server controller interface used to interact with the server.
     */
    void execute(NetworkSession caller, ServerControllerInterface controller);
}
