package it.polimi.ingsw.gc12.Network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents the main server interface for RMI communication in a distributed system.
 * <p>
 * This interface defines the methods that the main server should implement to handle RMI client connections.
 * It is intended to be implemented by the class responsible for accepting connections made by clients over the network.
 * </p>
 */
public interface RMIMainServer extends Remote {

    /**
     * Accepts a remote client connection request and returns a virtual server instance for communication over RMI technology.
     *
     * @param client The remote client requesting connection.
     * @return A virtual server instance for further communication with the client.
     * @throws RemoteException if there is a communication-related exception.
     */
    RMIVirtualServer accept(RMIVirtualClient client) throws RemoteException;
}
