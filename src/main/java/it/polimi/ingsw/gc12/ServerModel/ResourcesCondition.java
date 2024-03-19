package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.EnumMap;

/*
A condition that counts how many sets of resources of the specified type the player has
 */
public class ResourcesCondition implements PointsCondition {

    /*
    The resources to be evaluated
     */
    private final EnumMap<Resource, Integer> CONDITION;

    /*
    Generates the condition from the list of resources passed as parameter
     */
    public ResourcesCondition(EnumMap<Resource, Integer> condition) {
        this.CONDITION = new EnumMap<>(condition);
    }

    /*
    Returns the list of resources of this condition
     */
    protected EnumMap<Resource, Integer> getConditionParameters() {
        return new EnumMap<>(CONDITION);
    }

    /*
    Counts how many disjoint sets of resources can be set apart among the target player's resources
     */
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: add try catch here?
        return CONDITION.keySet().stream()
                .mapToInt((resourceType) -> (int) (
                                target.getOwnedResources()
                                        .get(resourceType)
                        /
                                        CONDITION.get(resourceType)
                        )
                ).min()
                .getAsInt();
    }
}

// numberOfTimesSatisfied() -> Si test
//                             - Casi limite
//                               thisCard undefined
//                               target undefined
