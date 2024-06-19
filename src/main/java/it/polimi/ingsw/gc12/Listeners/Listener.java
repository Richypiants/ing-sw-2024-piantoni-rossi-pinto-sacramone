package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Commands.Command;

/**
 * Represents a listener that handles notifications received by an external source.
 * <p>
 * The {@code Listener} interface represents an entity responsible for handling notifications.
 * An implementation receives notifications by having its notified() method called: thus, it must be properly
 * overridden and implemented by subclasses .
 * </p>
 */
public interface Listener {
    /**
     * Notifies the listener of a received command.
     * <p>
     * Subclasses should override this method by defining a proper notification routine.
     * </p>
     *
     * @param command The command notified to the listener.
     */
    void notified(Command command);
}
