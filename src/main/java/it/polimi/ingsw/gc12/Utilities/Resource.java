package it.polimi.ingsw.gc12.Utilities;

/**
An enumeration of all the possible "resources" values in card corners and back-centers
 */
public enum Resource {

    NOT_A_CORNER(" ", -1), EMPTY(" ", 223),
    WOLF("W", 32), BUTTERFLY("B", 207),
    GRASS("G", 82), MUSHROOM("M", 88),
    INK("I", 94), FEATHER("F", 94),
    SCROLL("S", 94);

    public final String SYMBOL;

    public final int ANSI_COLOR;

    /**
     *  ANSI_COLOR: -1 is used as a placeholder swapped during the card rendering with the card background
     *  */

    Resource(String symbol, int ansiColor){
        this.SYMBOL = symbol;
        this.ANSI_COLOR = ansiColor;
    }
}


