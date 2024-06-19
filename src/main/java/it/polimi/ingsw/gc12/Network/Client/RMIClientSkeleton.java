package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.RMIMainServer;
import it.polimi.ingsw.gc12.Network.RMIVirtualClient;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * The {@code RMIClientSkeleton} class represent the skeleton that the Server must own for handling communication with a remote RMI client.
 */
public class RMIClientSkeleton extends NetworkSession implements RMIVirtualClient {

    /**
     * Constructs a new {@code RMIClientSkeleton} instance.
     *
     * @param controller The controller interface for managing client operations.
     */
    private RMIClientSkeleton(ControllerInterface controller) {
        super(controller);
        try {
            //System.setProperty("java.rmi.server.hostname", ipClient);

            Registry registry = LocateRegistry.getRegistry(Client.getClientInstance().serverIPAddress, 5001);
            UnicastRemoteObject.exportObject(this, 0);
            Client.getClientInstance().serverConnection =
                    ((RMIMainServer) registry.lookup("codex_naturalis_rmi")).accept(this);

            //If connection to the server is successful, I wake up the connect() function continuously retrying to
            // reconnect every 10 seconds
            synchronized (ViewState.getCurrentState()) {
                ViewState.getCurrentState().notifyAll();
            }
        } catch (RemoteException | NotBoundException e) {
            Client.getClientInstance().resetClient();
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new instance of RMIClientSkeleton containing the singleton instance of the ClientController of this client.
     *
     * @return The instance of RMIClientSkeleton.
     */
    public static RMIClientSkeleton getInstance() {
        return new RMIClientSkeleton(ClientController.getInstance());
    }

    /**
     * Sends a command from the server to this client for execution.
     *
     * @param command The command to be executed by this client.
     * @throws RemoteException if there is a communication-related exception.
     */
    @Override
    public void requestToClient(ClientCommand command) throws RemoteException {
        Client.getClientInstance().commandsReceivedExecutor.submit(() -> command.execute(ClientController.getInstance()));
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
        //TODO: maybe set something here for this class too?
        return null;
    }
}
