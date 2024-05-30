package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt to perform an operation on a deck is called on an empty one.
 */
public class EmptyDeckException extends Exception {

    /**
     * Constructs a new EmptyDeckException with {@code null} as its detail message.
     */
    public EmptyDeckException() {
    }

    /**
     * Constructs a new EmptyDeckException with the specified detail message.
     *
     * @param message the detail message.
     */
    public EmptyDeckException(String message) {
        super(message);
    }
}
