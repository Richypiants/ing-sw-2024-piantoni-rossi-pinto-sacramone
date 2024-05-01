package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class BroadcastMessageCommand implements ServerCommand {

    private final String MESSAGE;

    public BroadcastMessageCommand(String message) {
        MESSAGE = message;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) {
        serverController.broadcastMessage(caller, MESSAGE);
    }
}
