package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;

//TODO: add documentation comments

public class ResourcesCondition implements PointsCondition {
    private ArrayList<Resource> condition;

    public ResourcesCondition(ArrayList<Resource> condition) {
        //TODO: safe copy of the condition arraylist?
        //this.condition = condition;
    }

    protected ArrayList<Resource> getConditionParameters() {
        return new ArrayList<Resource>(condition);
    }

    //FIXME: interfaces only allow public methods...
    protected int numberOfTimesSatisfied(InGamePlayer of) {
        //TODO: pattern match logic here
        return 0;
    }
}
