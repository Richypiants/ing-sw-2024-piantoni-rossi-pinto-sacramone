package it.polimi.ingsw.gc12.Model.Conditions;

import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

/**
A condition that counts how many corners are covered when playing the associated card
 */
public class CornersCondition implements PointsCondition {

    /**
    Counts how many corners are covered when playing the associated card
     */
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: check for exceptions!
        GenericPair<Integer, Integer> coordinates = target.getCardCoordinates((PlayableCard) thisCard);

        // If the card thisCard has been played in position (x, y), the (at most) four covered cards in the
        // corners must be in position (x +- 1, y +- 1) (if they are present)
        //FIXME: int casting?
        return (int) ((PlayableCard) thisCard).getCorners(Side.FRONT).keySet().stream()
                .filter((offset) -> target.getPlacedCards()
                        .containsKey(new GenericPair<>(
                                coordinates.getX() + offset.getX(),
                                coordinates.getY() + offset.getY()
                                )
                        )
                ).count();
    }

    public String toString() {
        return "(CornersCondition) ";
    }
}

// numberOfTimesSatisfied() -> Si test
//                             - Casi limite
//                               thisCard undefined
//                               target undefined
