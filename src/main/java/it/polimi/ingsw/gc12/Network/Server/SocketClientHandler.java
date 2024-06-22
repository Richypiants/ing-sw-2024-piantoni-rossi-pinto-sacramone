package it.polimi.ingsw.gc12.Network.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Commands.Command;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.SocketHandler;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Network.VirtualServer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;

/**
 * The {@code SocketClientHandler} class represents a handler for managing communication with the client connected to this server.
 * It extends {@link SocketHandler} and implements {@link VirtualServer}.
 */
public class SocketClientHandler extends SocketHandler implements VirtualClient {

    /**
     * Constructs a new SocketServerHandler instance.
     *
     * @param socket     The socket associated with the client connection.
     * @param controller The controller interface for managing server operations.
     * @throws IOException if an I/O error occurs while creating the socket or streams.
     */
    public SocketClientHandler(Socket socket, ControllerInterface controller) throws IOException {
        super(socket, controller);
    }

    /**
     * Sends a command from this handler to the connected client.
     *
     * @param command The command to be sent to the client.
     */
    @Override
    public void requestToClient(ClientCommand command) throws IOException {
        sendRequest(command);
    }

    /**
     * Executes the received command from the connected client.
     *
     * @param receivedCommand The command received from the client.
     */
    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        try {
            Server.getInstance().commandExecutorsPool.submit(
                    () -> ((ServerCommand) receivedCommand).execute(
                            this, (ServerControllerInterface) getController()
                    )
            );
        } catch (RejectedExecutionException e) {
            try {
                this.getListener().notified(new ThrowExceptionCommand(
                        new RejectedExecutionException("This server is currently overloaded, shutting down connection: try again later..."))
                );
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * Prints the error message generated during client-server communication.
     *
     * @param e The exception representing the error.
     */
    @Override
    public void printError(Exception e) {
        e.printStackTrace();
    }

    /**
     * Creates a listener for the network session.
     * <p>
     * Subclasses should implement this method to create a listener specific to the objects and events they would like to listen on.
     * </p>
     *
     * @param session The network session for which to create the listener.
     * @return The listener associated with the session.
     */
    @Override
    protected NetworkListener createListener(NetworkSession session) {
        return new ServerListener(this, this);
    }
}
