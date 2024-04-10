package it.polimi.ingsw.gc12.Utilities.Exceptions;

public class InvalidCardPositionException extends Exception{
    public InvalidCardPositionException() {
    }

    public InvalidCardPositionException(String message) {
        super(message);
    }
}
