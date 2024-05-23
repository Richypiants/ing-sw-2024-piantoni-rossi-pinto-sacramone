package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Model.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.InGamePlayer;

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
    public ObjectiveCard(int id, int pointsGranted, PointsCondition condition) {
        super(id, pointsGranted);
        this.POINTS_CONDITION = condition;
    }

    /**
    Returns the number of points the target InGamePlayer is awarded upon evaluating this card's objective points
    by calculating how many times the points' condition is satisfied
     */
    @Override
    public int awardPoints(InGamePlayer target){
        return (this.POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target));
    }

    //FIXME: unsafe return
    public PointsCondition getPointsCondition(){
        return this.POINTS_CONDITION;
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
