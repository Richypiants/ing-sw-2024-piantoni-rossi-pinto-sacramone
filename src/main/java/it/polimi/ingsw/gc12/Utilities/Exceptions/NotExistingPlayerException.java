package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when a request to perform an action involves a player who is unknown to the server.
 * This typically occurs when a player attempts to perform an action, but the server does not recognize
 * the player's nickname making the request or the player targeted by the request.
 */
public class NotExistingPlayerException extends Exception{

    /**
     * Constructs a new NotExistingPlayerException with {@code null} as its detail message.
     */
    public NotExistingPlayerException() {
    }

    /**
     * Constructs a new NotExistingPlayerException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NotExistingPlayerException(String message) {
        super(message);
    }
}
