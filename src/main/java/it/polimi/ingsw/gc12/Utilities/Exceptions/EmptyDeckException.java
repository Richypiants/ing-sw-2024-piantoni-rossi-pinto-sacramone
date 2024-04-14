package it.polimi.ingsw.gc12.Utilities.Exceptions;

public class EmptyDeckException extends Exception {
    public EmptyDeckException(String message) {
        super(message);
    }

    public EmptyDeckException() {
    }
}
