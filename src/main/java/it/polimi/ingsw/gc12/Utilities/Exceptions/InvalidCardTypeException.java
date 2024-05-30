package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt to perform a specific operation which requires a specific type of card with an incompatible one is made.
 */
public class InvalidCardTypeException extends Exception{

    /**
     * Constructs a new InvalidCardTypeException with {@code null} as its detail message.
     */
    public InvalidCardTypeException() {
    }

    /**
     * Constructs a new InvalidCardTypeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidCardTypeException(String message) {
        super(message);
    }
}
