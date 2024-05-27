package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.VirtualClient;

public class DirectMessageCommand implements ServerCommand {

    private final String RECEIVER_NAME;
    private final String MESSAGE;

    public DirectMessageCommand(String receiverName, String message) {
        this.RECEIVER_NAME = receiverName;
        this.MESSAGE = message;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) {
        serverController.directMessage(caller, RECEIVER_NAME, MESSAGE);
    }
}
