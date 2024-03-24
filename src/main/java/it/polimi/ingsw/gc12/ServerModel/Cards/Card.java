package it.polimi.ingsw.gc12.ServerModel.Cards;

import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Image;

/*
A template for a standard card object
 */
public abstract class Card {

    /*
    A unique card identifier that facilitates the card's retrieval
     */
    //FIXME: is this really necessary?
    public final int ID;

    /*
    The number of points granted to a player upon playing this card (0 for cards that do not have points physically
    portrayed on the artwork)
     */
    public final int POINTS_GRANTED;

    /*
    The front image for this card
     */
    public final Image FRONT_SPRITE;

    /*
    The back image for this card
     */
    public final Image BACK_SPRITE;

    /*
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

    /*
    Returns the number of points granted to player target who has just played the card (default for cards
    without points conditions)
     */
    public int awardPoints(InGamePlayer target) {
        return this.POINTS_GRANTED;
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