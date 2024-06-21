package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents a virtual server interface for RMI communication in a distributed system.
 * <p>
 * This interface extends both the {@code Remote} interface and the {@code VirtualServer} interface.
 * It defines methods that a virtual RMI server should implement to handle incoming commands from clients and for closing the server.
 * </p>
 */
public interface RMIVirtualServer extends Remote, VirtualServer {

    /**
     * Sends a command to the RMI server for processing.
     *
     * @param command The command to be processed by the RMI server.
     * @throws RemoteException if there is a communication-related exception.
     */
    @Override
    void requestToServer(ServerCommand command) throws RemoteException;

    /**
     * Closes the communication over an RMI channel.
     * <p>
     * Due to the internal implementation of RMI, a channel cannot be directly closed, but this method has to be implemented
     * to guarantee consistency and adherence with the {@code VirtualServer} interface contract.
     * </p>
     * @throws RemoteException if there is a communication-related exception.
     */
    @Override
    void close() throws RemoteException;
}
