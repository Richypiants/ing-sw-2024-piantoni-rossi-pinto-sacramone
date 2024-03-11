package it.polimi.ingsw.gc12.ServerModel;

//TODO: add documentation comments

public class PatternCondition implements PointsCondition {
    private ArrayList<Triplet<Integer, Integer, Resource>> condition;

    public PatternCondition(ArrayList<Triplet<Integer, Integer, Resource>> condition) {
        //TODO: safe copy of the condition arraylist?
        //this.condition = condition;
    }

    protected ArrayList<Triplet<Integer, Integer, Resource>> getConditionParameters() {
        return new ArrayList<Triplet<Integer, Integer, Resource>>(condition);
    }

    //FIXME: interfaces only allow public methods...
    protected int numberOfTimesSatisfied(InGamePlayer of) {
        //TODO: pattern match logic here
        return 0;
    }
}
