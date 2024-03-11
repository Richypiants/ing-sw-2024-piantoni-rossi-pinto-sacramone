package it.polimi.ingsw.gc12.ServerModel;

// The following class represents a general card template, with all the game cards' common attributes and methods
public abstract class Card {
    public final int ID; // Unique card identifier to easily retrieve card from "DB"
    public final int POINTS_GRANTED; // Points granted to player upon playing this card (0 for not specified)
    public final Image FRONT_SPRITE; // Front image for this card
    public final Image BACK_SPRITE; // Back image for this card
    private Side shownSide;

    // Generic card constructor, will receive parameters parsed from JSON
    public Card(int id, int pointsGranted, Image frontSprite, Image backSprite) {
        this.ID = id;
        this.POINTS_GRANTED = pointsGranted;
        //FIXME: don't know if they should be passed like this...
        this.FRONT_SPRITE = frontSprite;
        this.BACK_SPRITE = backSprite;
    }

    // Getter method for "side"
    public Side getShownSide() {
        return shownSide;
    }

    // Setter method for "side"
    public void setShownSide(Side side) {
        this.shownSide = side;
    }

    // Generic template for awarding card points to the player upon playing this card
    public int awardPoints() {
        return this.POINTS_GRANTED;
    }
}
