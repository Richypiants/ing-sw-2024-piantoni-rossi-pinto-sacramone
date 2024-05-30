package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when attempting to perform an action that is forbidden or not allowed
 * by the state of the game or the context in which the player is.
 */
public class ForbiddenActionException extends Exception {

    /**
     * Constructs a new ForbiddenActionException with {@code null} as its detail message.
     */
    public ForbiddenActionException() {
    }

    /**
     * Constructs a new ForbiddenActionException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ForbiddenActionException(String message) {
        super(message);
    }
}
