package it.polimi.ingsw.gc12.Utilities;

/*
An enumeration of a card's placement (UNDEFINED when the card has not been played)
 */
public enum Side {
    /*
    FIXME: is UNDEFINED still needed if we remove it from card and only put it in, let's say, the field?
     */
    FRONT, BACK, UNDEFINED;
}
