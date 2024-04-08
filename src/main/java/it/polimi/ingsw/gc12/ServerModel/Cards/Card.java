package it.polimi.ingsw.gc12.ServerModel.Cards;

import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Image;

/**
 A template representing a standard card object
 */
public abstract class Card {

    /**
     A unique card identifier to facilitate the card's retrieval
     */
    //FIXME: is this really necessary? ---> yes, helps debugging
    public final int ID;

    /**
    The number of points granted to a player upon playing this card (0 for cards that do not have points physically
    portrayed on the artwork)
     */
    public final int POINTS_GRANTED;

    /**
    The front image for this card
     */
    public final Image FRONT_SPRITE;

    /**
    The back image for this card
     */
    public final Image BACK_SPRITE;

    /**
    Constructs instances of Card's subclasses by initializing the attributes they have in common
     */
    //FIXME: I don't particularly like this description...
    public Card(int id, int pointsGranted, Image frontSprite, Image backSprite) {
        this.ID = id;
        this.POINTS_GRANTED = pointsGranted;
        //FIXME: this will depend on how Images will be implemented
        this.FRONT_SPRITE = frontSprite;
        this.BACK_SPRITE = backSprite;
    }

    /**
    Returns the number of points granted to player target who has just played the card (default for cards
    without points conditions)
     */
    public abstract int awardPoints(InGamePlayer target);

    public String toString() {
        return "{" +
                "ID=" + ID +
                ", POINTS_GRANTED=" + POINTS_GRANTED +
                ", FRONT_SPRITE=" + FRONT_SPRITE +
                ", BACK_SPRITE=" + BACK_SPRITE +
                '}';
    }
}

// getShownSide() (Getter) -> No test
// setShownSide() (Setter) -> Si test
//                            - Casi limite
//                              side undefined
//
// awardPoints() -> Si test
//                  - Casi limite
//                    target undefined