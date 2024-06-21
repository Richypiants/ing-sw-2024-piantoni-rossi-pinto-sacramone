package it.polimi.ingsw.gc12.Network.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.RMIVirtualClient;
import it.polimi.ingsw.gc12.Network.RMIVirtualServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.RejectedExecutionException;

/**
 * The {@code RMIServerStub} class represents the stub that the client must own for handling communication with a remote RMI Server.
 * It extends {@link NetworkSession} and implements {@link RMIVirtualServer}.
 */
public class RMIServerStub extends NetworkSession implements RMIVirtualServer {

    /**
     * The RMI virtual client associated with this server stub.
     */
    public final RMIVirtualClient CLIENT;

    /**
     * Constructs a new RMIServerStub instance.
     *
     * @param client     The RMI virtual client connected to the server.
     * @param controller The controller interface for managing server operations.
     */
    public RMIServerStub(RMIVirtualClient client, ControllerInterface controller) {
        super(controller);
        this.CLIENT = client;
        this.listener = createListener(this);
        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[SOCKET]: New connection accepted from {" + client + "}");
    }

    /**
     * Creates a listener for the network session.
     *
     * @param session The network session for which to create the listener.
     * @return The listener associated with the session.
     */
    @Override
    protected NetworkListener createListener(NetworkSession session) {
        return new ServerListener(this, CLIENT);
    }

    /**
     * Sends a command from the client to the server.
     *
     * @param command The command to be sent to the server.
     * @throws RemoteException if there is a communication-related exception.
     */
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
                        new RejectedExecutionException("This server is currently overloaded, shutting down connection: try again later..."))
                );
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * Closes the communication over the RMI channel.
     * <p>
     * Due to the internal implementation of RMI, a channel cannot be directly closed, but this method has to be implemented
     * to guarantee consistency and adherence with the {@code RMIVirtualServer} interface contract.
     * <\p>
     */
    @Override
    public void close() {
    }
}
