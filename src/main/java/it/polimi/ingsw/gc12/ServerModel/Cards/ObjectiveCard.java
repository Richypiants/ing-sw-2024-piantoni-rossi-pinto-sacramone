package it.polimi.ingsw.gc12.ServerModel.Cards;

import it.polimi.ingsw.gc12.ServerModel.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Image;

/**
A template for a generic Objective Card in this game's cards set
 */
public final class ObjectiveCard extends Card {

    /**
    The condition to be evaluated when calculating objective points
     */
    private final PointsCondition POINTS_CONDITION;

    /**
    Generates an Objective card from the given parameters
     */
    public ObjectiveCard(int id, int pointsGranted, Image frontSprite, Image backSprite, PointsCondition condition) {
        super(id, pointsGranted, frontSprite, backSprite);
        this.POINTS_CONDITION = condition; //FIXME: copy this?
    }


    /**
    Generates an Objective card from the one passed as parameter
     */
    //FIXME: why do we need this???
    public ObjectiveCard(ObjectiveCard card) {
        super(card.ID, card.POINTS_GRANTED, card.FRONT_SPRITE, card.BACK_SPRITE);
        this.POINTS_CONDITION = card.POINTS_CONDITION;
    }

    /**
    Returns the number of points the target InGamePlayer is awarded upon evaluating this card's objective points
    by calculating how many times the points' condition is satisfied
     */
    @Override
    public int awardPoints(InGamePlayer target){
        return (this.POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target));
    }

    @Override
    public String toString() {
        return "(ObjectiveCard) " + super.toString() +
                " {" +
                "POINTS_CONDITION=" + POINTS_CONDITION +
                "} ";
    }
}

// awardPoints() -> Si test
//                  - Casi limite (awardPoints è un ovverride)
//                    target undefined
