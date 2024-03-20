package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.EnumMap;

/*
A template for a Gold card in the game's card set
 */
public class GoldCard extends PlayableCard {

    /*
    The condition which is evaluated when calculating total points granted upon playing this card
     */
    public final PointsCondition POINTS_CONDITION;

    /*
    The resources needed to play this card
     */
    public final ResourcesCondition RESOURCES_NEEDED_TO_PLAY;

    /*
    Generates a gold card from the passed parameters
     */
    public GoldCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                    EnumMap<Resource, Integer> centerBackResources, PointsCondition pointsCondition,
                    ResourcesCondition resourcesNeededToPlay) {
        super(id, pointsGranted, frontSprite, backSprite, centerBackResources, corners);
        this.POINTS_CONDITION = pointsCondition; //FIXME: should we copy this?
        this.RESOURCES_NEEDED_TO_PLAY = resourcesNeededToPlay; //FIXME: and should we copy this too?
    }

    /*
    Returns the list of resources needed to play this card
     */
    public ResourcesCondition getNeededResourcesToPlay() {
        //FIXME: unsafe?
        return RESOURCES_NEEDED_TO_PLAY;
    }

    /*
    Returns the number of points the target InGamePlayer is awarded upon playing this Gold Card by calculating
    how many times the points' condition is satisfied
     */
    @Override
    public int awardPoints(InGamePlayer target){
        return POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target);
    }

    @Override
    public String toString() {
        return "GoldCard{" +
                "POINTS_CONDITION=" + POINTS_CONDITION +
                ", RESOURCES_NEEDED_TO_PLAY=" + RESOURCES_NEEDED_TO_PLAY +
                "} " + super.toString();
    }
}

// getNeededresourcesToPlay() (Getter) -> No test
// awardPoints() -> Si test
//                  - Statement coverage (i test per numberOfTimesSatisfied() sono sufficienti)
//
//                  - Casi limite
//                    target undefined