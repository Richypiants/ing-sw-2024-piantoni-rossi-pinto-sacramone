package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.ArrayList;

//TODO: add documentation comments

public class ResourcesCondition implements PointsCondition {
    private final ArrayList<Resource> CONDITION;

    public ResourcesCondition(ArrayList<Resource> condition) {
        this.CONDITION = new ArrayList<Resource>(condition);
    }

    protected ArrayList<Resource> getConditionParameters() {
        return new ArrayList<Resource>(CONDITION);
    }

    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: add try catch here?
        return CONDITION.stream()
                .distinct()
                .mapToInt((resourceType) -> target.getOwnedResources().get(resourceType)
                        /
                        (int) CONDITION.stream()
                                .filter((conditionResource) -> conditionResource.equals(resourceType))
                                .count()
                ).min()
                .getAsInt();
    }
}
