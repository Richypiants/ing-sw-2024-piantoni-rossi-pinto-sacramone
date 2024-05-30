package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt is made to set a card that has already been set.
 * For example, another request of selecting the secret objective is made while this was already done.
 */
public class AlreadySetCardException extends Exception {

    /**
     * Constructs a new AlreadySetCardException with {@code null} as its detail message.
     */
    public AlreadySetCardException() {}

    /**
     * Constructs a new AlreadySetCardException with the specified detail message.
     *
     * @param message the detail message.
     */
    public AlreadySetCardException(String message) {
        super(message);
    }
}
