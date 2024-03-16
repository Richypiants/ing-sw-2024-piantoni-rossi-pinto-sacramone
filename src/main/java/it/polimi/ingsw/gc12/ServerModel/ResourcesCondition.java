package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.ArrayList;

//TODO: add documentation comments

public class ResourcesCondition implements PointsCondition {
    private ArrayList<Resource> condition;

    public ResourcesCondition(ArrayList<Resource> condition) {
        this.condition = new ArrayList<Resource>(condition);
    }

    protected ArrayList<Resource> getConditionParameters() {
        return new ArrayList<Resource>(condition);
    }

    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: add try catch here?
        return condition.stream()
                .distinct()
                .mapToInt((resourceType) -> target.getOwnedResources().get(resourceType)
                        /
                        (int) condition.stream()
                                .filter((conditionResource) -> conditionResource.equals(resourceType))
                                .count()
                ).min()
                .getAsInt();
    }
}
