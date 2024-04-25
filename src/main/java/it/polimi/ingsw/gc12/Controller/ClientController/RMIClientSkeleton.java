package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualClient;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientSkeleton implements RMIVirtualClient {

    private static RMIClientSkeleton SINGLETON_RMI_CLIENT = null;

    private RMIClientSkeleton() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5001);
            ClientController.getInstance().serverConnection =
                    ((RMIVirtualServer) registry.lookup("codex_naturalis_rmi"));
            UnicastRemoteObject.exportObject(this, 5002);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        ClientController.getInstance().thisClient = this;
    }

    public static RMIClientSkeleton getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        if(SINGLETON_RMI_CLIENT == null)
            SINGLETON_RMI_CLIENT = new RMIClientSkeleton();
        return SINGLETON_RMI_CLIENT;
    }

    @Override
    public void requestToClient(ClientCommand command) throws Exception {
        command.execute(ClientController.getInstance());

        //The first parameter of the update message is interpreted, then the correct action will be applied on the corresponding class of the model
        //TODO: The View has an observer over the model, which notifies incoming updates and then the view pulls the new infos and reloads the view.
    }
}
