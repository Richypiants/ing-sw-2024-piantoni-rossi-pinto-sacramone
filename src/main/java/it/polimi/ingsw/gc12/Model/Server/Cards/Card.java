package it.polimi.ingsw.gc12.Model.Server.Cards;

import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;

/**
 * Represents a generic card in the game, providing common properties that exist for all types of cards.
 * This class serves as a base class for specific card implementations.
 */
public abstract class Card {

    /**
     * A unique card identifier to facilitate the card's retrieval.
     */
    public final int ID;

    /**
     * The number of points a player receives when this card is played.
     * If the card does not directly grant points, this value is set to 0.
     */
    public final int POINTS_GRANTED;

    /**
     * Constructs a card with the specified unique identifier and point value.
     *
     * @param id            The unique identifier for the card.
     * @param pointsGranted The number of points granted by this card.
     */
    public Card(int id, int pointsGranted) {
        this.ID = id;
        this.POINTS_GRANTED = pointsGranted;
    }

    /**
     * Awards points to the specified player based on the card's conditions
     *
     * @param target The player who played the card.
     * @return The number of points to award to the player.
     */
    public abstract int awardPoints(InGamePlayer target);

    /*
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Card otherCard)) return false;
        return (this.ID == otherCard.ID);
    }*/

    /**
     * Returns a string representation of the card, including its ID and points granted.
     *
     * @return A string describing the card.
     */
    public String toString() {
        return "{" +
                "ID=" + ID +
                ", POINTS_GRANTED=" + POINTS_GRANTED +
                '}';
    }


}
