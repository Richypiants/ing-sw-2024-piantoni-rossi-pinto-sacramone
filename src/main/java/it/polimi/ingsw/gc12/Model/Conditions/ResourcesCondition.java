package it.polimi.ingsw.gc12.Model.Conditions;

import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.Map;

/**
A condition that counts how many sets of resources of the specified type the player has
 */
public class ResourcesCondition implements PointsCondition {

    /**
    The resources to be evaluated
     */
    private final Map<Resource, Integer> CONDITION;

    /**
    Generates the condition from the list of resources passed as parameter
     */
    public ResourcesCondition(Map<Resource, Integer> condition) {
        this.CONDITION = Map.copyOf(condition);
    }

    /**
    Returns the list of resources of this condition
     */
    protected Map<Resource, Integer> getConditionParameters() {
        return CONDITION;
    }

    /**
    Counts how many disjoint sets of resources can be set apart among the target player's resources
     */
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: add try catch here?
        return CONDITION.keySet().stream()
                .mapToInt((resourceType) -> target.getOwnedResources().get(resourceType)
                        /
                        CONDITION.get(resourceType)
                ).min()
                .getAsInt();
    }

    public String toString() {
        return "(ResourceCondition) {" +
                "CONDITION=" + CONDITION +
                "} ";
    }
}

// numberOfTimesSatisfied() -> Si test
//                             - Casi limite
//                               thisCard undefined
//                               target undefined
