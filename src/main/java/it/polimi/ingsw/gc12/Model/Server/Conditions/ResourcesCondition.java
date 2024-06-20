package it.polimi.ingsw.gc12.Model.Server.Conditions;

import it.polimi.ingsw.gc12.Model.Server.Cards.Card;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;

import java.util.Map;

/**
 * A condition that counts how many sets of resources of the specified type the player has.
 */
public class ResourcesCondition implements PointsCondition {

    /**
     * The resources to be evaluated.
     */
    private final Map<Resource, Integer> CONDITION;

    /**
     * Generates the condition from the list of resources passed as parameter.
     *
     * @param condition A map representing the resources and their quantities needed to satisfy the condition.
     */
    public ResourcesCondition(Map<Resource, Integer> condition) {
        this.CONDITION = Map.copyOf(condition);
    }

    /**
     * Returns the list of resources of this condition.
     *
     * @return The condition parameters.
     */
    public Map<Resource, Integer> getConditionParameters() {
        return CONDITION;
    }

    /**
     * Counts how many disjoint sets of resources can be set apart among the target player's resources.
     *
     * @param thisCard The card being played, which contains a ResourcesCondition.
     * @param target   The player who is playing the card.
     * @return The number of times the resource condition is satisfied.
     */
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        return CONDITION.keySet().stream()
                .mapToInt((resourceType) -> target.getOwnedResources().get(resourceType)
                        /
                        CONDITION.get(resourceType)
                ).min().orElseThrow();
    }


    /**
     * Returns a string representation of this condition.
     * The string representation provides a simple description indicating that this
     * is a ResourceCondition along with the resources needed to satisfy it .
     *
     * @return A string representation of this condition.
     */
    public String toString() {
        return "(ResourceCondition) {" +
                "CONDITION=" + CONDITION +
                "} ";
    }
}