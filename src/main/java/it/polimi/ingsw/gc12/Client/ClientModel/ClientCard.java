package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.Utilities.Image;

public class ClientCard {
    /**
     * A unique card identifier to facilitate the card's retrieval
     */
    //FIXME: is this really necessary?
    public final int ID;
    /**
     * The front image for this card
     */
    public final Image FRONT_SPRITE;
    /**
     * The back image for this card
     */
    public final Image BACK_SPRITE;
    /**
     * The front image for this card
     */
    public final Image FRONT_TUI_SPRITE;
    /**
     * The back image for this card
     */
    public final Image BACK_TUI_SPRITE;
}
