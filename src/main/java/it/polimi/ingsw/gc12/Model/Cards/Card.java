package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Image;

/**
 A template representing a standard card object
 */
public abstract class Card {

    /**
     A unique card identifier to facilitate the card's retrieval
     */
    public final int ID;

    /**
    The number of points granted to a player upon playing this card (0 for cards that do not have points physically
    portrayed on the artwork)
     */
    public final int POINTS_GRANTED;

    /**
    Constructs instances of Card's subclasses by initializing the attributes they have in common
     */
    //FIXME: I don't particularly like this description...
    public Card(int id, int pointsGranted) {
        this.ID = id;
        this.POINTS_GRANTED = pointsGranted;
        //FIXME: this will depend on how Images will be implemented
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