package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command to send a direct message from the server to a specific client.
 * Implements the {@link ServerCommand} interface.
 */
public class DirectMessageCommand implements ServerCommand {

    private final String RECEIVER_NAME;
    private final String MESSAGE;

    /**
     * Constructs a DirectMessageCommand to send a message to a specific client.
     *
     * @param receiverName The nickname of the client who will receive the message.
     * @param message      The message content to be sent.
     */
    public DirectMessageCommand(String receiverName, String message) {
        this.RECEIVER_NAME = receiverName;
        this.MESSAGE = message;
    }

    /**
     * Executes the command by requesting to the server controller to send a direct message to the specified client.
     *
     * @param caller           The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.directMessage(caller, RECEIVER_NAME, MESSAGE);
    }
}
