package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt to pick a card with and index greater than the number of possible choices is made.
 */
public class InvalidDeckPositionException extends Exception {

    /**
     * Constructs a new InvalidDeckPositionException with {@code null} as its detail message.
     */
    public InvalidDeckPositionException() {
    }

    /**
     * Constructs a new InvalidDeckPositionException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidDeckPositionException(String message) {
        super(message);
    }
}
