package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to throw an exception on the client side.
 * Implements the {@link ClientCommand} interface.
 */
public class ThrowExceptionCommand implements ClientCommand {

    private final Exception EXCEPTION;

    /**
     * Constructs a ThrowExceptionCommand with the specified exception.
     *
     * @param exception The exception to be thrown on the client side.
     */
    public ThrowExceptionCommand(Exception exception) {
        this.EXCEPTION = exception;
    }

    /**
     * Executes the command on the provided client controller, requesting to handle or throw the stored exception.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.throwException(EXCEPTION);
    }
}
