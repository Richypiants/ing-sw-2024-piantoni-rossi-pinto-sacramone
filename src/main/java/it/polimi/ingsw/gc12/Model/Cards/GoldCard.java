package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Model.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Map;

/**
A template for a Gold card in the game's card set
 */
public class GoldCard extends PlayableCard {

    /**
    The condition which is evaluated when calculating total points granted upon playing this card
     */
    private final PointsCondition POINTS_CONDITION;

    /**
    The resources needed to play this card
     */
    private final ResourcesCondition RESOURCES_NEEDED_TO_PLAY;

    /**
    Generates a gold card from the passed parameters
     */
    public GoldCard(int id, int pointsGranted, Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners,
                    Map<Resource, Integer> centerBackResources, PointsCondition pointsCondition,
                    ResourcesCondition resourcesNeededToPlay) {
        super(id, pointsGranted, centerBackResources, corners);
        this.POINTS_CONDITION = pointsCondition; //FIXME: should we copy this?
        this.RESOURCES_NEEDED_TO_PLAY = resourcesNeededToPlay; //FIXME: and should we copy this too?
    }

    /**
    Returns the list of resources needed to play this card
     */
    public ResourcesCondition getNeededResourcesToPlay() {
        //FIXME: unsafe?
        return RESOURCES_NEEDED_TO_PLAY;
    }

    public PointsCondition getPointsCondition(){
        //FIXME: unsafe?
        return POINTS_CONDITION;
    }

    /**
    Returns the number of points the target InGamePlayer is awarded upon playing this Gold Card by calculating
    how many times the points' condition is satisfied
     */
    @Override
    public int awardPoints(InGamePlayer target){
        if (POINTS_CONDITION == null) {
            return POINTS_GRANTED;
        } else {
            return POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target);
        }
    }

    @Override
    public String toString() {
        return "(GoldCard) " + super.toString() +
                " {" +
                "POINTS_CONDITION=" + POINTS_CONDITION +
                ", RESOURCES_NEEDED_TO_PLAY=" + RESOURCES_NEEDED_TO_PLAY +
                "} ";
    }
}

// getNeededresourcesToPlay() (Getter) -> No test
// awardPoints() -> Si test
//                  - Statement coverage (i test per numberOfTimesSatisfied() sono sufficienti)
//
//                  - Casi limite
//                    target undefined