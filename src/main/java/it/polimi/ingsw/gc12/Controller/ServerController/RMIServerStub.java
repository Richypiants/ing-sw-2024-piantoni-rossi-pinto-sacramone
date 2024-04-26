package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualServer;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.RejectedExecutionException;

public class RMIServerStub implements RMIVirtualServer {

    private static final RMIServerStub SINGLETON_RMI_SERVER = new RMIServerStub();

    private RMIServerStub() {
        try {
            UnicastRemoteObject.exportObject(this, 5001);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static RMIServerStub getInstance() {
        return SINGLETON_RMI_SERVER;
    }

    @Override
    public void requestToServer(VirtualClient caller, ServerCommand command) throws RemoteException {
        System.out.println("[RMI][CLIENT]: Request from " + caller);
        try {
            Server.getInstance().commandExecutorsPool.submit(() -> command.execute(caller, ServerController.getInstance()));
        } catch (RejectedExecutionException e) {
            //TODO: implement answer
        }
    }

}
