package it.polimi.ingsw.gc12.Utilities;

/**
An enumeration of all the possible "resources" values in card corners and back-centers
 */
public enum Resource {

    NOT_A_CORNER(" ", -1), EMPTY(" ", -1),
    ANIMAL("A", 32), INSECT("I", 207),
    PLANT("P", 82), FUNGI("F", 88),
    INK("K", 94), QUILL("Q", 94),
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


