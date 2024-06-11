package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;

/**
 * The {@code VirtualClient} interface represents a client entity in a networked environment,
 * providing a contract for communication between the server and the client.
 * <p>
 * This interface abstracts the communication protocol and network infrastructure,
 * allowing server-side components to interact with clients without being tightly coupled
 * to specific implementation details.
 * </p>
 * <p>
 * Implementations of this interface are expected to encapsulate
 * the details of establishing and managing communication with clients, including
 * low-level networking operations.
 * </p>
 * <p>
 * The commands exchanged between the client and server follow a predefined structure, as defined by the {@link ClientCommand} interface.
 * </p>
 *
 * @see ClientCommand for a standard structure of commands exchanged over the network.
 */
public interface VirtualClient {

    /**
     * Sends a command from the server to the client.
     * <p>
     * Implementations of this method should encapsulate the details of how the command
     * is sent over the network, for example through Serialization, and any necessary error handling mechanisms.
     * </p>
     *
     * @param command The command to be sent to the client.
     * @throws Exception if an error occurs during communication with the client.
     */
    void requestToClient(ClientCommand command) throws Exception;
}
