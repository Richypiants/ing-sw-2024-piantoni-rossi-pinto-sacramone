package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class ThrowExceptionCommand implements ClientCommand {

    private final Exception EXCEPTION;

    public ThrowExceptionCommand(Exception exception) {
        this.EXCEPTION = exception;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.throwException(EXCEPTION);
    }
}
