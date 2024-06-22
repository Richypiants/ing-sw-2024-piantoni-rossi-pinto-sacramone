package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command to broadcast a message from the server to all clients.
 * Implements the {@link ServerCommand} interface.
 */
public class BroadcastMessageCommand implements ServerCommand {

    private final String MESSAGE;

    /**
     * Constructs a BroadcastMessageCommand with the specified message.
     *
     * @param message The message to be broadcasted to all clients.
     */
    public BroadcastMessageCommand(String message) {
        MESSAGE = message;
    }

    /**
     * Executes the command by requesting the server controller to broadcast the message to all clients.
     *
     * @param caller         The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.broadcastMessage(caller, MESSAGE);
    }
}
