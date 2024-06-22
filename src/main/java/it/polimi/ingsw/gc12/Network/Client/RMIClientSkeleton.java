package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.RMIMainServer;
import it.polimi.ingsw.gc12.Network.RMIVirtualClient;

import java.rmi.NoSuchObjectException;
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
     * The singleton instance of the RMIClientSkeleton.
     */
    private static RMIClientSkeleton SINGLETON_RMI_CLIENT = null;

    /**
     * Constructs a new {@code RMIClientSkeleton} instance.
     *
     * @param controller The controller interface for managing client operations.
     */
    private RMIClientSkeleton(ControllerInterface controller) throws RemoteException, NotBoundException {
        super(controller);
        //FIXME: remove IP!
        //System.setProperty("java.rmi.server.hostname", ipClient);

        Registry registry = LocateRegistry.getRegistry(Client.getClientInstance().serverIPAddress, 5001);
        UnicastRemoteObject.exportObject(this, 0);
        Client.getClientInstance().serverConnection =
                ((RMIMainServer) registry.lookup("codex_naturalis_rmi")).accept(this);
    }

    /**
     * Creates a new instance of RMIClientSkeleton containing the singleton instance of the ClientController of this client.
     *
     * @return The instance of RMIClientSkeleton.
     */
    public static RMIClientSkeleton getInstance() {
        synchronized (RMIClientSkeleton.class) {
            if (SINGLETON_RMI_CLIENT == null) {
                try {
                    SINGLETON_RMI_CLIENT = new RMIClientSkeleton(ClientController.getInstance());
                } catch (RemoteException | NotBoundException e) {
                    ClientController.getInstance().ERROR_LOGGER.log(e);
                }
            }
            return SINGLETON_RMI_CLIENT;
        }
    }

    /**
     * Sends a command from the server to this client for execution.
     *
     * @param command The command to be executed by this client.
     * @throws RemoteException if there is a communication-related exception.
     */
    @Override
    public void requestToClient(ClientCommand command) throws RemoteException {
        Client.getClientInstance().commandsReceivedExecutor.submit(() -> {
            try {
                command.execute(ClientController.getInstance());
            } catch (Exception e) {
                close();
                ClientController.getInstance().ERROR_LOGGER.log(e);
            }
        });
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

    public void close() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ignored) {
            //Already unexported? Not a problem, we don't care
        } finally {
            SINGLETON_RMI_CLIENT = null;
            Client.getClientInstance().session = null;
            Client.getClientInstance().serverConnection = null;
        }
    }
}
