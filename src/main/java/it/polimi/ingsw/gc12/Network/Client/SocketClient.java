package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Network.VirtualServer;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a client socket connection to a server in a networked environment.
 * This class facilitates communication with the server by sending commands and receiving responses.
 */
public class SocketClient implements VirtualServer {

    /**
     * The singleton instance of the SocketClient.
     * */
    private static SocketClient SINGLETON_SOCKET_CLIENT = null;

    /**
     * The handler for server communication.
     * */
    private static SocketServerHandler serverHandler = null;

    /**
     * The executor service which performs continuous reads from the server.
     * */
    public final ExecutorService readerExecutor;

    /**
     * Constructs a new SocketClient, initializing the connection to the server and starts a thread to handle incoming messages.
     *
     * @throws IOException if an I/O error occurs while establishing the connection.
     */
    private SocketClient() throws IOException {
        this.readerExecutor = Executors.newSingleThreadExecutor();

        Socket socket = new Socket(Client.getClientInstance().serverIPAddress, 5000);

        //If connection to the server is successful no exception is thrown; the program can get to the following line
        // and I wake up the ConnectionSetupState.connect() function, which has been continuously retrying to reconnect
        // every 5 seconds
        synchronized (ViewState.class) {
            ViewState.class.notifyAll();
        }

        serverHandler = new SocketServerHandler(socket, ClientController.getInstance());

        readerExecutor.submit(
                () -> {
                    while (true) {
                        try {
                            serverHandler.read();
                        } catch (IOException e) {
                            close();
                            ClientController.getInstance().ERROR_LOGGER.log(e);
                            break;
                        }
                    }
                }
        );
    }

    /**
     * Returns the singleton instance of the SocketClient.
     *
     * @return The singleton instance of the SocketClient.
     */
    public static SocketClient getInstance() {
        synchronized (SocketClient.class) {
            if (SINGLETON_SOCKET_CLIENT == null) {
                try {
                    SINGLETON_SOCKET_CLIENT = new SocketClient();
                } catch (IOException e) {
                    ClientController.getInstance().ERROR_LOGGER.log(e);
                }
            }
        }
        return SINGLETON_SOCKET_CLIENT;
    }

    /**
     * Closes the connection to the server.
     */
    public void close() {
        SINGLETON_SOCKET_CLIENT = null;
        serverHandler.close();
        Client.getClientInstance().serverConnection = null;
    }

    /**
     * Sends a command to the server.
     *
     * @param command The command to be sent to the server.
     */
    @Override
    public void requestToServer(ServerCommand command) {
        serverHandler.requestToServer(command);
    }
}
