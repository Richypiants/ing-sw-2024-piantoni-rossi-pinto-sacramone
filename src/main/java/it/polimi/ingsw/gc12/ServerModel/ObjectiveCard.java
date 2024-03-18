package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;

/*
A template for a generic Objective Card in this game's cards set
 */
public class ObjectiveCard extends Card {

    /*
    The condition to be evaluated when calculating objective points
     */
    private final PointsCondition POINTS_CONDITION;

    /*
    Generates an Objective card from the given parameters
     */
    public ObjectiveCard(int id, int points, Image frontSprite, Image backSprite, PointsCondition condition) {
        super(id, points, frontSprite, backSprite);
        this.POINTS_CONDITION = condition; //FIXME: copy this?
    }

    /*
    Generates an Objective card from the one passed as parameter
     */
    //FIXME: why do we need this???
    public ObjectiveCard(ObjectiveCard card) {
        super(card.ID, card.POINTS_GRANTED, card.FRONT_SPRITE, card.BACK_SPRITE);
        this.POINTS_CONDITION = card.POINTS_CONDITION;
    }

    /*
    Returns the number of points the target InGamePlayer is awarded upon evaluating this card's objective points
    by calculating how many times the points' condition is satisfied
     */
    @Override
    public int awardPoints(InGamePlayer target){
        return (this.POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target));
    }
}

// awardPoints() -> Si test
//                  - Casi limite (awardPoints è un ovverride)
//                    target undefined
