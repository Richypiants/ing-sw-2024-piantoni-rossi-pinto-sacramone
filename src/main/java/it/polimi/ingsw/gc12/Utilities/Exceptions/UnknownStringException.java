package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when the received string does not match any of the expected strings in the given context.
 */
public class UnknownStringException extends Exception {

    /**
     * Constructs a new UnknownStringException with {@code null} as its detail message.
     */
    public UnknownStringException() {
    }

    /**
     * Constructs a new UnknownStringException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UnknownStringException(String message) {
        super(message);
    }
}
