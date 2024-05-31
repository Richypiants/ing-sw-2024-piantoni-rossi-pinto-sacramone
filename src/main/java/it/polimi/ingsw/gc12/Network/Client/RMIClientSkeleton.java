package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.RMIMainServer;
import it.polimi.ingsw.gc12.Network.RMIVirtualClient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientSkeleton extends NetworkSession implements RMIVirtualClient {

    private static RMIClientSkeleton SINGLETON_RMI_CLIENT = null;

    private RMIClientSkeleton(ControllerInterface controller) {
        super(controller);
        try {
            //System.setProperty("java.rmi.server.hostname", ipClient);

            Registry registry = LocateRegistry.getRegistry(ClientController.getInstance().serverIPAddress, 5001);
            UnicastRemoteObject.exportObject(this, 0);
            ClientController.getInstance().serverConnection =
                    ((RMIMainServer) registry.lookup("codex_naturalis_rmi")).accept(this);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        ClientController.getInstance().thisClient = this;
    }

    public static RMIClientSkeleton getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        SINGLETON_RMI_CLIENT = new RMIClientSkeleton(ClientController.getInstance());
        return SINGLETON_RMI_CLIENT;
    }

    @Override
    public void requestToClient(ClientCommand command) throws RemoteException {
        ClientController.getInstance().commandExecutorsPool.submit(() -> command.execute(ClientController.getInstance()));

        //The first parameter of the update message is interpreted, then the correct action will be applied on the corresponding class of the model
        //TODO: The View has an observer over the model, which notifies incoming updates and then the view pulls the new infos and reloads the view.
    }

    @Override
    protected Listener createListener() {
        //TODO: maybe set something here for this class too?
        return null;
    }
}
