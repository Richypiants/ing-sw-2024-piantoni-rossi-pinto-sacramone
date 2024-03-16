package it.polimi.ingsw.gc12.ServerModel;

//TODO: complete from UML and add comments for documentation

import it.polimi.ingsw.gc12.Utilities.Image;

public class ObjectiveCard extends Card {
    private final PointsCondition POINTS_CONDITION;


    public ObjectiveCard(int id, int points, Image frontSprite, Image backSprite, PointsCondition condition) {
        super(id, points, frontSprite, backSprite);
        this.POINTS_CONDITION = condition;
    }

    public ObjectiveCard( ObjectiveCard card){
        super(card.ID, card.POINTS_GRANTED, card.FRONT_SPRITE, card.BACK_SPRITE);
        this.POINTS_CONDITION = card.POINTS_CONDITION;
    }

    //Specific Override for Super Class Method
    public int awardPoints(InGamePlayer target){
        return (this.POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target));
    }
}
