package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Commands.Command;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Interface that represents a client command to be executed on the client side.
 * Extends the {@link Command} interface.
 */
public interface ClientCommand extends Command {

    /**
     * Executes the client command using the provided client controller.
     *
     * @param controller The client controller interface on which to execute the command.
     */
    void execute(ClientControllerInterface controller);
}
