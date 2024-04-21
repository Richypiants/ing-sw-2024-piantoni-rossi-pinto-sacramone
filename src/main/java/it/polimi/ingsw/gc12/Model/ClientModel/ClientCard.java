package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Utilities.Picture;

public class ClientCard {

    /**
     * A unique card identifier to facilitate the card's retrieval
     */
    public final int ID;
    /**
     * The front image for this card
     */
    public final Picture FRONT_SPRITE;
    /**
     * The back image for this card
     */
    public final Picture BACK_SPRITE;
    /**
     * The front image for this card
     */
    public final Picture FRONT_TUI_SPRITE; //TODO: Obviously not an Image
    /**
     * The back image for this card
     */
    public final Picture BACK_TUI_SPRITE; //TODO: Obviously not an Image

    public ClientCard(int id, Picture front, Picture back, Picture tuiFront, Picture tuiBack) {
        this.ID = id;
        this.FRONT_SPRITE = front;
        this.BACK_SPRITE = back;
        this.FRONT_TUI_SPRITE = tuiFront;
        this.BACK_TUI_SPRITE = tuiBack;
    }

}
