package it.polimi.ingsw.gc12.Utilities.Exceptions;

/**
 * Exception thrown when an attempt to place a card with a ResourceCondition without the resources needed to perform this action is made.
 */
public class NotEnoughResourcesException extends Exception{

    /**
     * Constructs a new NotEnoughResourcesException with {@code null} as its detail message.
     */
    public NotEnoughResourcesException(){
    }

    /**
     * Constructs a new NotEnoughResourcesException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NotEnoughResourcesException(String message){
        super(message);
    }
}