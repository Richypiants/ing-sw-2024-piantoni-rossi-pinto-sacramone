package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt is made to place a card on a coordinate pair
 * which is already full or is invalid by the constraints given by the field displacement.
 */
public class InvalidCardPositionException extends Exception{

    /**
     * Constructs a new InvalidCardPositionException with {@code null} as its detail message.
     */
    public InvalidCardPositionException() {
    }

    /**
     * Constructs a new InvalidCardPositionException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidCardPositionException(String message) {
        super(message);
    }
}
