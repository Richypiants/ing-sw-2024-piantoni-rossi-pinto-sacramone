package it.polimi.ingsw.gc12.Model.Server.Conditions;

import it.polimi.ingsw.gc12.Model.Server.Cards.Card;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

/**
 * A condition that counts how many corners are covered when playing the associated card.
 * This condition is used to determine the number of corners of existing cards that are covered
 * when a new card is placed on the game field. This can be used for calculating points or
 * fulfilling specific game objectives.
 */
public class CornersCondition implements PointsCondition {

    /**
     * Counts how many corners are covered when playing the associated card.
     * This method calculates the number of corners covered by the card being played by checking
     * the surrounding positions on the game field. It considers the four possible corners
     * (top-left, top-right, bottom-left, bottom-right) around the card's placement.
     *
     * @param thisCard The card being played.
     * @param target The player who is playing the card.
     * @return The number of corners covered when the card is placed.
     */
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        GenericPair<Integer, Integer> coordinates = target.getCardCoordinates((PlayableCard) thisCard);

        // If the card has been played in position (x, y), the (at most) four covered cards in the
        // corners, if they exist, must be in position (x +- 1, y +- 1).
        return (int) ((PlayableCard) thisCard).getCorners(Side.FRONT).keySet().stream()
                .filter((offset) -> target.getPlacedCards()
                        .containsKey(new GenericPair<>(
                                coordinates.getX() + offset.getX(),
                                coordinates.getY() + offset.getY()
                                )
                        )
                ).count();
    }

    /**
     * Returns a string representation of this condition.
     * The string representation provides a simple description indicating that this
     * is a CornersCondition, useful for debugging purposes.
     * </p>
     *
     * @return A string representation of this condition.
     */
    public String toString() {
        return "(CornersCondition) ";
    }
}
