package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;

/*
A condition that counts how many corners are covered when playing the associated card
 */
public class CornersCondition implements PointsCondition {

    /*
    Counts how many corners are covered when playing the associated card
     */
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: check for exceptions!
        int count = 0;
        GenericPair<Integer, Integer> coordinates = target.getOwnField()
                .getCardCoordinates((PlayableCard) thisCard);

        // If the card thisCard has been played in position (x, y), the (at most) four covered cards in the
        // corners must be in position (x +- 1, y +- 1) (if they are present)
        for(int i = -1; i <= 1; i += 2){
            for(int j = -1; j <= 1; j += 2){
                if (target.getPlacedCards()
                        .containsKey(new GenericPair<Integer, Integer>(
                                coordinates.getX() + i,
                                coordinates.getY() + j
                                )
                        )
                )
                    count++;
            }
        }
        return count;
    }
}

// numberOfTimesSatisfied() -> Si test
//                             - Casi limite
//                               thisCard undefined
//                               target undefined
