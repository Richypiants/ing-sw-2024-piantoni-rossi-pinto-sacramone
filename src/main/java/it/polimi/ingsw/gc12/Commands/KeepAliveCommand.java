package it.polimi.ingsw.gc12.Commands;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a command used to maintain session persistence between client and server.
 * Implements both {@link ServerCommand} and {@link ClientCommand} interfaces.
 * <p>
 * This command serves a similar function to TCP keep-alive messages, ensuring that the
 * network session remains active and preventing premature disconnection due to inactivity by executing this action.
 * It acts as a rudimentary form of session management protocol by periodically verifying
 * the connectivity status and acknowledging the persistence of the session.
 * </p>
 * <p>
 * On the server side, the {@link #execute(NetworkSession, ServerControllerInterface)} method is called
 * to handle the keep-alive request from the client, maintaining the session alive in response.
 * </p>
 * <p>
 * On the client side, the {@link #execute(ClientControllerInterface)} method is invoked to signal
 * that the client session should remain active, preventing automatic disconnection due to inactivity.
 * </p>
 */
public class KeepAliveCommand implements ServerCommand, ClientCommand {

    /**
     * Constructs a KeepAliveCommand.
     */
    public KeepAliveCommand() {}

    /**
     * Executes the command on the server side, handling the keep-alive request from the client.
     * It ensures that the network session remains active.
     *
     * @param caller          The network session that initiated the keep-alive request.
     * @param serverController The server controller interface responsible for managing the session.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.keepAlive(caller);
    }

    /**
     * Executes the command on the client side, signaling to maintain the session active.
     * It prevents the client from being disconnected due to inactivity.
     *
     * @param clientController The client controller interface responsible for managing the session.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.keepAlive();
    }
}




