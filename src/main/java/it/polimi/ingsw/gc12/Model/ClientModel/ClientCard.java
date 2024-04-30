package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.fusesource.jansi.Ansi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class ClientCard implements Serializable {
    //TODO: dovrà mergearsi con Card

    /**
     * A unique card identifier to facilitate the card's retrieval
     */
    public final int ID;
    /**
     * The front image for this card
     */
    public final String FRONT_SPRITE;
    /**
     * The back image for this card
     */
    public final String BACK_SPRITE;

    public final Map<Side, ArrayList<ArrayList<Triplet<String, Integer[], Integer>>>> TUI_SPRITES;

    public ClientCard(int id, String front, String back,
                      Map<Side, ArrayList< ArrayList<Triplet<String, Integer[], Integer>>>> tuiSprites) {
        this.ID = id;
        this.FRONT_SPRITE = front;
        this.BACK_SPRITE = back;
        this.TUI_SPRITES = tuiSprites;
    }
}
