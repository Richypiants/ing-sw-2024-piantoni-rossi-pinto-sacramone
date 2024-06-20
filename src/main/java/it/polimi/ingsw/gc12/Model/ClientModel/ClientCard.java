package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a client-side card in the game, including its unique identifier,
 * graphical user interface (GUI) sprites, and text user interface (TUI) sprites.
 * This class is used to display the card's appearance and relevant details to the player.
 */
public class ClientCard implements Serializable {

    /**
     * A unique card identifier to facilitate the card's retrieval.
     */
    public final int ID;

    /**
     * A map of the card's image resources path, keyed by side.
     * This is used for the graphical display
     * of the card in the user interface.
     */
    public final Map<Side, String> GUI_SPRITES;

    /**
     * A map representing the card's appearance in the TUI (Text User Interface) view.
     * Each card side is visualized through two entries, each corresponding to one side of the card.
     * The external list represents the rows of the card (in this project, each card has 5 rows).
     * Each row contains a list of triplets, where each triplet represents a visual segment of the row.
     *
     * The triplet consists of:
     * - A string: typically a character (either a space or a printable character).
     * - An array of integers: representing the foreground and background color codes.
     * - An integer: indicating the length for which this visual pattern should be repeated.
     *
     * For example, if a row in a card consists entirely of the same character and color, the length will
     * reflect the number of characters to be displayed on the screen.
     */
    public final Map<Side, ArrayList<ArrayList<Triplet<String, Integer[], Integer>>>> TUI_SPRITES;


    /**
     * Overrides the {@code equals} method to provide custom equality logic for {@code ClientCard} objects.
     *
     * @param other The object to compare with this {@code ClientCard}.
     * @return {@code true} if the given object has the same ID of this {@code ClientCard}, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ClientCard otherCard)) return false;
        return (this.ID == otherCard.ID);
    }

    /**
     * Constructs a new ClientCard with the specified ID, GUI sprites, and TUI sprites.
     *
     * @param id the unique identifier for this card
     * @param guiSprites a map of the card's GUI sprites
     * @param tuiSprites a map of the card's TUI sprites
     */
    public ClientCard(int id, Map<Side, String> guiSprites,
                      Map<Side, ArrayList< ArrayList<Triplet<String, Integer[], Integer>>>> tuiSprites) {
        this.ID = id;
        this.GUI_SPRITES = guiSprites;
        this.TUI_SPRITES = tuiSprites;
    }
}
