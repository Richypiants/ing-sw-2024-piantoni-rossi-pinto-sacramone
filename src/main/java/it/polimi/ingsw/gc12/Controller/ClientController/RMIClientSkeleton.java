package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Utilities.RMIVirtualClient;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualMethod;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualServer;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;

public class RMIClientSkeleton implements VirtualServer, RMIVirtualClient {

    private static RMIClientSkeleton SINGLETON_RMI_CLIENT = null;
    private final Map<String, RMIVirtualMethod> serverMethods;

    protected RMIClientSkeleton() {
        try {
            Registry registry = LocateRegistry.getRegistry("???", 5001);
            this.serverMethods = ((RMIVirtualServer) registry.lookup("codex_naturalis_rmi_methods")).getMap();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static RMIClientSkeleton getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        if(SINGLETON_RMI_CLIENT == null)
            SINGLETON_RMI_CLIENT = new RMIClientSkeleton();
        return SINGLETON_RMI_CLIENT;
    }

    @Override
    public void requestToClient(ArrayList<Object> arguments) {
        try {
            serverMethods.get((String) arguments.removeFirst()).invokeWithArguments(this, arguments);
        } catch (Throwable t) {
            //TODO: gestire Throwable
            throw new RuntimeException();
        }
    }

    @Override
    public void requestToServer(ArrayList<Object> objects) throws Throwable {
        ClientController.commandHandles.get((String) objects.removeFirst()).invokeWithArguments(objects);

        //The first parameter of the update message is interpreted, then the correct action will be applied on the corresponding class of the model
        //TODO: The View has an observer over the model, which notifies incoming updates and then the view pulls the new infos and reloads the view.
    }
}
