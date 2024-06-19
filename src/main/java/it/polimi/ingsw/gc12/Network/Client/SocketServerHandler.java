package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.Command;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.SocketHandler;
import it.polimi.ingsw.gc12.Network.VirtualServer;

import java.io.IOException;
import java.net.Socket;

/**
 * The {@code SocketServerHandler} class represents a handler for managing communication with the server connected to this client.
 * It extends {@link SocketHandler} and implements {@link VirtualServer}.
 */
public class SocketServerHandler extends SocketHandler implements VirtualServer {

    /**
     * Constructs a new SocketServerHandler instance.
     *
     * @param socket     The socket associated with the server connection.
     * @param controller The controller interface for managing client operations.
     * @throws IOException if an I/O error occurs while creating the socket or streams.
     */
    public SocketServerHandler(Socket socket, ControllerInterface controller) throws IOException {
        super(socket, controller);
    }

    /**
     * Sends a command from this handler to the connected server.
     *
     * @param command The command to be sent to the server.
     */
    @Override
    public void requestToServer(ServerCommand command) {
        try {
            sendRequest(command);
        } catch (IOException e) {
            printError(e);
        }
    }

    /**
     * Executes the received command from the connected server.
     *
     * @param receivedCommand The command received from the server.
     */
    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        Client.getClientInstance().commandsReceivedExecutor.submit(
                () -> {
                    try {
                        ((ClientCommand) receivedCommand).execute((ClientControllerInterface) getController());
                    } catch (Exception e) {
                        printError(e);
                    }
                }
        );
    }

    /**
     * Prints the error message generated during client-server communication.
     *
     * @param e The exception representing the error.
     */
    @Override
    public void printError(Exception e){
        ClientController.getInstance().ERROR_LOGGER.log(e);
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
        return null;
    }
}
