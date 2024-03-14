package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;

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
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: pattern match logic here
        target.getOwnField().entrySet().stream()
                .filter((entry) -> !entry.getKey().equals(new GenericPair<Integer, Integer>(0, 0)))
                .filter((entry) -> entry.getValue()
                        .getCenterBackResources()
                        .getFirst()
                        .equals(condition.getFirst().getZ())
                )
                .filter((entry) -> getConditionParameters().subList(1, condition.size())
                        .stream()
                        .map((a) -> target.getOwnField().get(
                                new GenericPair<Integer, Integer>(
                                        entry.getKey().getX() + a.getX(),
                                        entry.getKey().getY() + a.getY()
                                )
                        ).getCenterBackResources().getFirst().equals(a.getZ()))
                        .reduce(true,
                                (a, b) -> a && b)
                ).algoritmoDellAlbero();

        return 0;
    }
}
