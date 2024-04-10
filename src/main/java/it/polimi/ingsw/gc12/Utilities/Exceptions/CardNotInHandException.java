package it.polimi.ingsw.gc12.Utilities.Exceptions;

public class CardNotInHandException extends Exception{
    public CardNotInHandException(String message){
        super(message);
    }

    public CardNotInHandException(){
    }

}
