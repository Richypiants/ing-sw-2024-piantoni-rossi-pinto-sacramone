package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;

/**
 * A marker interface for objects that can have listeners attached to them.
 * <p>
 * The {@code Listenable} interface defines the contract for objects that can
 * manage listeners. Implementing classes must provide methods to add and remove
 * listeners, as well as a method to notify all registered listeners of a particular event.
 * The thread-safety of these operations is typically the responsibility
 * of the classes that implement this interface, especially in concurrent or
 * multi-threaded environments.
 * </p>
 */
public interface Listenable {

    /**
     * Adds a listener to the list of listeners.
     *
     * @param listener The listener to add.
     */
    void addListener(Listener listener);

    /**
     * Removes a listener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    void removeListener(Listener listener);

    /**
     * Notifies all registered listeners of a particular event.
     *
     * @param command The command associated with the event.
     */
    void notifyListeners(ClientCommand command);
}
