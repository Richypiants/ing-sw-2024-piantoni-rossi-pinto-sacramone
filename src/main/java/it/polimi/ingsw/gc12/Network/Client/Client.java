package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualServer;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

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
    protected final ExecutorService commandsReceivedExecutor = Executors.newSingleThreadExecutor();

    /**
     * The single executor for sending outgoing commands to the server without blocking the view or controller.
     */
    private ExecutorService commandSenderExecutor;

    public String serverIPAddress;
    public VirtualServer serverConnection;
    public NetworkSession session;
    public Thread keepAlive;

    public final Object DISCONNECTED_LOCK = new Object();
    public boolean disconnected;

    private Client() {
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
            ClientController.getInstance().ERROR_LOGGER.log(e);
        }
        if (keepAlive != null)
            keepAlive.interrupt();
        this.serverIPAddress = "localhost";
        this.serverConnection = null;
        this.session = null;
        this.keepAlive = null;
        this.disconnected = true;

        if (commandSenderExecutor != null)
            this.commandSenderExecutor.shutdownNow();
        this.commandSenderExecutor = Executors.newSingleThreadExecutor();
    }

    public void setupCommunication(String serverIPAddress, String communicationTechnology) {
        Client.getClientInstance().serverIPAddress = serverIPAddress;
        commandSenderExecutor.submit(() -> {
            synchronized (ViewState.getCurrentState()) {
                switch (communicationTechnology.trim().toLowerCase()) {
                    case "socket" -> Client.getClientInstance().serverConnection = SocketClient.getInstance();
                    case "rmi" -> Client.getClientInstance().session = RMIClientSkeleton.getInstance();
                    default ->
                            ViewState.printError(new RuntimeException("Communication technology " + communicationTechnology + " not supported"));
                }
            }
        });
    }

    //Helper method to catch RemoteException (and eventually other ones) only one time
    public void requestToServer(ServerCommand command) {
        commandSenderExecutor.submit(() -> {
            try {
                serverConnection.requestToServer(command);
                //This notified is needed to make the Client exit from the quittingScreen when quitting
                synchronized (this) {
                    this.notifyAll();
                }
            } catch (Exception e) {
                ClientController.getInstance().ERROR_LOGGER.log(e);
            }
        });
    }
}
