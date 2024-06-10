package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt is made to join a lobby that is already full.
 */
public class FullLobbyException extends Exception {

    /**
     * Constructs a new FullLobbyException with {@code null} as its detail message.
     */
    public FullLobbyException() {
    }

    /**
     * Constructs a new FullLobbyException with the specified detail message.
     *
     * @param message the detail message.
     */
    public FullLobbyException(String message) {
        super(message);
    }
}
