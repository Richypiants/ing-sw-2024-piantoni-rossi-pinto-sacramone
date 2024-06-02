package it.polimi.ingsw.gc12.Network.Server;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.RMIVirtualClient;
import it.polimi.ingsw.gc12.Network.RMIVirtualServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.RejectedExecutionException;

public class RMIServerStub extends NetworkSession implements RMIVirtualServer {

    public final RMIVirtualClient CLIENT;

    public RMIServerStub(RMIVirtualClient client, ControllerInterface controller) {
        super(controller);
        this.CLIENT = client;
        this.listener = createListener(this);
        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Listener createListener(NetworkSession session) {
        return new Listener(this, CLIENT);
    }

    @Override
    public void requestToServer(ServerCommand command) throws RemoteException {
        System.out.println("[RMI][CLIENT]: Request from " + CLIENT);
        try {
            Server.getInstance().commandExecutorsPool.submit(
                    () -> command.execute(this, (ServerControllerInterface) getController())
            );
        } catch (RejectedExecutionException e) {
            try {
                this.getListener().notified(new ThrowExceptionCommand(
                        //FIXME: not ideal to shut connection down... maybe don't accept more than a predecided number of connections?
                        new RejectedExecutionException("This server is currently busy, shutting down connection: try again later..."))
                );
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void close() {
    }
}
