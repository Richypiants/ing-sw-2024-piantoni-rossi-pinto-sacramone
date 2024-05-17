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

    public final Map<Side, String> GUI_SPRITES;

    public final Map<Side, ArrayList<ArrayList<Triplet<String, Integer[], Integer>>>> TUI_SPRITES;

    public ClientCard(int id, Map<Side, String> guiSprites,
                      Map<Side, ArrayList< ArrayList<Triplet<String, Integer[], Integer>>>> tuiSprites) {
        this.ID = id;
        this.GUI_SPRITES = guiSprites;
        this.TUI_SPRITES = tuiSprites;
    }
}
