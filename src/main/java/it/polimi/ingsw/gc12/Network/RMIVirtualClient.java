package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents a virtual client interface for RMI communication in a distributed system.
 * <p>
 * This interface extends both the {@code Remote} interface and the {@code VirtualClient} interface.
 * It defines the method that a virtual RMI client should implement to handle and forward incoming commands from the server.
 * </p>
 */
public interface RMIVirtualClient extends Remote, VirtualClient {

    /**
     * Sends a command to the RMI client for processing.
     *
     * @param command The command to be processed by the RMI client.
     * @throws RemoteException if there is a communication-related exception.
     */
    @Override
    void requestToClient(ClientCommand command) throws RemoteException;
}
