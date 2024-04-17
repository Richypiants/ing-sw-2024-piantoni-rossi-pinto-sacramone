package it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class ThrowExceptionCommand implements ClientCommand {

    private final Exception EXCEPTION;

    public ThrowExceptionCommand(Exception exception) {
        this.EXCEPTION = exception;
    }

    @Override
    public void execute(ClientControllerInterface clientController) throws Exception {
        clientController.throwException(EXCEPTION);
    }
}
