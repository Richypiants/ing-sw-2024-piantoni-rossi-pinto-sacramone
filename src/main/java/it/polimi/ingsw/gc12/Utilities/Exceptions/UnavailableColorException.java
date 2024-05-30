package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when a request to select a color which is already taken by someone else is made.
 */
public class UnavailableColorException extends Exception {

    /**
     * Constructs a new UnavailableColorException with {@code null} as its detail message.
     */
    public UnavailableColorException() {
    }

    /**
     * Constructs a new UnavailableColorException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UnavailableColorException(String message) {
        super(message);
    }
}
