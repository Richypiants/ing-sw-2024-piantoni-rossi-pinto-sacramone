package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.io.IOException;

/**
 * Represents a listener that handles notifications sent by the server to clients.
 * <p>
 * The {@code ServerListener} class represents an entity responsible for handling notifications sent by the server to clients.
 * It encapsulates a network session and a virtual client to which the notifications are forwarded. When a notification
 * is received, it is passed to the virtual client to be sent over the network. In case of communication errors or
 * other exceptions, appropriate actions are taken to ensure the consistency of the game.
 * </p>
 */
public class ServerListener extends NetworkListener {

    /**
     * The VirtualClient representing the connection implementation chosen by this client.
     */
    private final VirtualClient CLIENT;

    /**
     * Constructs a new listener with the specified network session and virtual client.
     *
     * @param session The network session associated with the listener.
     * @param client  The virtual client associated with the listener.
     */
    public ServerListener(NetworkSession session, VirtualClient client) {
        super(session);
        this.CLIENT = client;
    }

    /**
     * Retrieves the virtual client associated with this listener.
     *
     * @return The virtual client.
     */
    public VirtualClient getVirtualClient() {
        return this.CLIENT;
    }

    /**
     * Notifies the virtual client of a received command.
     * <p>
     * This method forwards the specified command to the virtual client associated with this listener.
     * In case of communication errors, such as IOExceptions, the method takes appropriate actions to ensure
     * the consistency of the game. If communication is closed, indicating that the target client has lost connection,
     * the method triggers the execution of the timeout task in the associated network session. This is done to prevent further errors due to the
     * inability to forward updates to the VirtualClient associated to this listener, which is no longer available and synchronized with the incremental updates.
     * Furthermore, after the execution of this task, the disconnected player will be able anytime to rejoin the game without receiving errors that lock him off
     * due to the connection to the server of this inconsistent instance with his nickname.
     * </p>
     *
     * @param command The command to be sent to the client.
     */
    @Override
    public void notified(Command command) {
        try {
            CLIENT.requestToClient((ClientCommand) command);
        } catch (IOException e) {
            //If communication is closed, the target has lost an update, so in case he reconnects, its game is inconsistent.
            //We must act to keep the game consistent
            //so the TimeoutTask routine has to be instantly executed.
            getSession().runTimeoutTimerTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
