package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Side;

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
    The side facing upwards after the card has been played (undefined when the card still hasn't been played)
     */
    private Side shownSide;

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
        this.shownSide = Side.UNDEFINED;
    }

    /*
    Returns the side which is facing upwards (undefined when card hasn't been played)
     */
    public Side getShownSide() {
        return this.shownSide;
    }

    /*
    Changes the side which is facing upwards
     */
    public void setShownSide(Side newSide) {
        this.shownSide = newSide;
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