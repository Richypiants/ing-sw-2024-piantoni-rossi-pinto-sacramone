package it.polimi.ingsw.gc12.Model.Server.Cards;

import it.polimi.ingsw.gc12.Model.Server.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;

/**
 * Represents an Objective Card in the game's card set. Objective Cards have specific conditions
 * that determine how many points they award to the player.
 */
public final class ObjectiveCard extends Card {

    /**
     * The condition evaluated to determine the total points granted upon playing this card.
     */
    private final PointsCondition POINTS_CONDITION;

    /**
     * Constructs an Objective Card with the specified parameters.
     *
     * @param id            The unique identifier for the card.
     * @param pointsGranted The base points granted by this card.
     * @param condition     The condition that determines how many points are the players awarded by
     *                      placing this card on its front side.
     */
    public ObjectiveCard(int id, int pointsGranted, PointsCondition condition) {
        super(id, pointsGranted);
        this.POINTS_CONDITION = condition;
    }

    /**
     * Calculates and returns the number of points awarded to the specified player upon evaluating this card's
     * objective points. The points are calculated based on how many times the points condition is satisfied.
     *
     * @param target The player who played the card.
     * @return The number of points awarded.
     */
    @Override
    public int awardPoints(InGamePlayer target){
        return (this.POINTS_GRANTED * POINTS_CONDITION.numberOfTimesSatisfied(this, target));
    }

    /**
     * Returns the points condition for this card.
     *
     * @return The points condition.
     */
    public PointsCondition getPointsCondition(){
        return this.POINTS_CONDITION;
    }

    /**
     * Returns a string representation of the Objective Card, including its unique ID, points granted,
     * and points condition.
     *
     * @return A string representation of the Objective Card.
     */
    @Override
    public String toString() {
        return "(ObjectiveCard) " + super.toString() +
                " {" +
                "POINTS_CONDITION=" + POINTS_CONDITION +
                "} ";
    }
}
