package it.polimi.ingsw.gc12.Utilities.Exceptions;

public class InvalidCardTypeException extends Exception{ //TODO: estende un suo sottotipo?

    public InvalidCardTypeException() {
    }

    public InvalidCardTypeException(String message) {
        super(message);
    }
}
