package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code Client} class represents a client in a client-server communication setup.
 */
public class Client {

    private final static Client CLIENT_INSTANCE = new Client();

    /**
     * The single executor for handling all the received commands from the server.
     */
    protected final ExecutorService commandsReceivedExecutor;

    /**
     * The single executor for sending outgoing commands to the server without blocking the view or controller.
     */
    private final ExecutorService commandSenderExecutor;

    public String serverIPAddress;
    public VirtualServer serverConnection;
    public NetworkSession session;
    public Thread keepAlive;

    private Client() {
        this.commandsReceivedExecutor = Executors.newSingleThreadExecutor();
        this.commandSenderExecutor = Executors.newSingleThreadExecutor();

        resetClient();
    }

    public static Client getClientInstance() {
        return CLIENT_INSTANCE;
    }

    public void resetClient() {
        try {
            if (serverConnection != null)
                serverConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (keepAlive != null)
            keepAlive.interrupt();
        this.serverIPAddress = "localhost";
        this.serverConnection = null;
        this.session = null;
        this.keepAlive = null;
    }

    //Helper method to catch RemoteException (and eventually other ones) only one time
    public void requestToServer(ServerCommand command) {
        commandSenderExecutor.submit(() -> {
            try {
                serverConnection.requestToServer(command);
                synchronized (this) {
                    this.notifyAll();
                }
            } catch (Exception e) {
                ClientController.getInstance().ERROR_LOGGER.log(e);
            }
        });
    }
}
