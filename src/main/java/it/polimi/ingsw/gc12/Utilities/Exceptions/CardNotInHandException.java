package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an action which requires to own the card in hand to execute it is done without satisfying this condition.
 */
public class CardNotInHandException extends Exception{

    /**
     * Constructs a new CardNotInHandException with {@code null} as its detail message.
     */
    public CardNotInHandException() {}

    /**
     * Constructs a new CardNotInHandException with the specified detail message.
     *
     * @param message the detail message.
     */
    public CardNotInHandException(String message){
        super(message);
    }
}
