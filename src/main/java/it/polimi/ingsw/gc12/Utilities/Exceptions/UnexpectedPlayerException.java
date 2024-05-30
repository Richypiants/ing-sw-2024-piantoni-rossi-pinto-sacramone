package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when a request to perform an action involves a player who is not supposed to perform such action at that moment.
 * For example, if it's Player X's turn and Player Y tries to perform an action that is doable for them,
 * but only on their turn and not during other players' turns.
 */
public class UnexpectedPlayerException extends Exception {

    /**
     * Constructs a new UnexpectedPlayerException with {@code null} as its detail message.
     */
    public UnexpectedPlayerException() {
    }

    /**
     * Constructs a new UnexpectedPlayerException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UnexpectedPlayerException(String message) {
        super(message);
    }
}
