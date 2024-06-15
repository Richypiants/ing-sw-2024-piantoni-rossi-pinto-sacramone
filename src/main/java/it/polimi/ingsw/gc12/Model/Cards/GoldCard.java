package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Model.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.Map;

/**
 * Represents a Gold Card in the game's card set, which grants points based on particular conditions
 * and requires resources to be played.
 */
public final class GoldCard extends PlayableCard {
    /**
     * The condition evaluated to determine the total points granted upon playing this card.
     */
    private final PointsCondition POINTS_CONDITION;

    /**
     * The resources required to play this card.
     */
    private final ResourcesCondition RESOURCES_NEEDED_TO_PLAY;

    /**
     * Constructs a Gold Card with the specified parameters.
     *
     * @param id                     The unique identifier for the card.
     * @param pointsGranted          The base number of points granted by the card.
     * @param corners                The resources associated with each corner of the card.
     * @param centerBackResources    The resources associated with the center back of the card.
     * @param pointsCondition        The condition that determines how many points are the players awarded by
     *                               placing this card on its front side
     * @param resourcesNeededToPlay  The resources required to play the card on its front side.
     */
    public GoldCard(int id, int pointsGranted, Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners,
                    Map<Resource, Integer> centerBackResources, PointsCondition pointsCondition,
                    ResourcesCondition resourcesNeededToPlay) {
        super(id, pointsGranted, centerBackResources, corners);
        this.POINTS_CONDITION = pointsCondition;
        this.RESOURCES_NEEDED_TO_PLAY = resourcesNeededToPlay;
    }

    /**
     * Returns the resources required to play this card on its front side.
     *
     * @return The resources needed to play this card.
     */
    public ResourcesCondition getNeededResourcesToPlay() {
        return RESOURCES_NEEDED_TO_PLAY;
    }


    /**
     * Returns the points condition for this card.
     *
     * @return The points condition.
     */
    public PointsCondition getPointsCondition(){
        return POINTS_CONDITION;
    }

    /**
     * Calculates and returns the number of points awarded to the specified player upon playing this Gold Card.
     * The points are calculated based on how many times the points condition is satisfied.
     *
     * @param target The player who played the card.
     * @return The number of points awarded.
     */
    @Override
    public int awardPoints(InGamePlayer target){
        if (POINTS_CONDITION == null) {
            return POINTS_GRANTED;
        } else {
            return POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target);
        }
    }

    /**
     * Returns a string representation of the Gold Card, including its unique ID, points granted,
     * points condition, and resources needed to play.
     *
     * @return A string representation of the Gold Card.
     */
    @Override
    public String toString() {
        return "(GoldCard) " + super.toString() +
                " {" +
                "POINTS_CONDITION=" + POINTS_CONDITION +
                ", RESOURCES_NEEDED_TO_PLAY=" + RESOURCES_NEEDED_TO_PLAY +
                "} ";
    }
}