package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;

/**
 * The {@code VirtualServer} interface represents a server entity in a networked environment,
 * providing a contract for communication between the server and clients.
 * <p>
 * This interface abstracts the communication protocol and network infrastructure,
 * allowing client-side components to interact with servers without being tightly coupled
 * to specific implementation details.
 * </p>
 * <p>
 * Implementations of this interface are expected to encapsulate
 * the details of establishing and managing communication with servers, including
 * low-level networking operations.
 * </p>
 * <p>
 * The commands exchanged between the client and server follow a predefined structure, as defined by the {@link ServerCommand} interface.
 * </p>
 *
 * @see ServerCommand for a standard structure of commands exchanged over the network.
 */
public interface VirtualServer {

    /**
     * Sends a command from a client to the server.
     * <p>
     * Implementations of this method should encapsulate the details of how the command
     * is sent over the network, for example through Serialization, and any necessary error handling mechanisms.
     * </p>
     *
     * @param command The command to be sent to the server.
     * @throws Exception if an error occurs during communication with the server.
     */
    void requestToServer(ServerCommand command) throws Exception;

    /**
     * Closes the communication channel with the server.
     * <p>
     * Implementations of this method should encapsulate the details of how the communication
     * channel is closed, such as releasing resources and terminating connections.
     */
    void close();
}
