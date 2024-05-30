package it.polimi.ingsw.gc12.Utilities;

/**
 * An enumeration of all the possible resource values that can appear in card corners and back-centers.
 */
public enum Resource {

    NOT_A_CORNER(" ", -1), EMPTY(" ", -1),
    ANIMAL("A", 32), INSECT("I", 207),
    PLANT("P", 82), FUNGI("F", 88),
    INK("K", 94), QUILL("Q", 94),
    SCROLL("S", 94);

    /**
     * The symbol or the character representing the resource.
     */
    public final String SYMBOL;

    /**
     * The ANSI color code for the resource.
     * Note that the value (-1) is used as a placeholder to indicate a character which maintains the standard background of that view,
     * or in others words nothing has to be done about the background.
     */
    public final int ANSI_COLOR;

    /**
     * Constructs a Resource with the given symbol and ANSI color code.
     *
     * @param symbol The symbol representing the resource.
     * @param ansiColor The ANSI color code for the resource.
     */
    Resource(String symbol, int ansiColor){
        this.SYMBOL = symbol;
        this.ANSI_COLOR = ansiColor;
    }
}


