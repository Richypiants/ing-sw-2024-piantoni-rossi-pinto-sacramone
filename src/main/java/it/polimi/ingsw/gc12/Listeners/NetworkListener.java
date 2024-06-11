package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a listener that handles notifications received by an external source.
 * <p>
 * The {@code Listener} class represents an entity responsible for handling notifications.
 * It encapsulates a network session representing the network actor that is listening to the notifications. A notification
 * is received by calling the notified() method, that still has to be overridden and implemented by subclasses as
 * requested from the implementation of the Listener interface.
 * </p>
 */
public abstract class NetworkListener implements Listener {

    /**
     * The NetworkSession associated to this network listener.
     */
    private final NetworkSession SESSION;

    /**
     * Constructs a new NetworkListener with the specified network session.
     *
     * @param session The network session associated with this network listener.
     */
    public NetworkListener(NetworkSession session) {
        this.SESSION = session;
    }

    /**
     * Retrieves the network session element associated with this network listener.
     *
     * @return The network session element.
     */
    public NetworkSession getSession() {
        return SESSION;
    }
}
