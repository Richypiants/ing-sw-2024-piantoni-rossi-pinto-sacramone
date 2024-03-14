package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

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
        return largestMaximumCompatibilityClass(
                new ArrayList<PlayableCard>(target.getOwnField().entrySet().stream()
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
                                .reduce(true, (a, b) -> a && b)
                        )
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList())
                )
        );
    }

    public int largestMaximumCompatibilityClass(ArrayList<PlayableCard> cards) {
        ArrayList<ArrayList<PlayableCard>> frontier = new ArrayList<ArrayList<PlayableCard>>();
        ArrayList<ArrayList<PlayableCard>> result = new ArrayList<ArrayList<PlayableCard>>();
        int nodesInLastLevel = 1, depth = 1;

        frontier.add(cards);

        while (!frontier.isEmpty()) {
            for (int i = 0; i < nodesInLastLevel; i++) {
                ArrayList<PlayableCard> tmp = frontier.removeFirst();
                frontier.add(new ArrayList<PlayableCard>(tmp.subList(1, tmp.size())));
                frontier.add(new ArrayList<PlayableCard>(
                        tmp.removeIf(
                                () ->
                        )
                );
            }

            for (int i = 0; i < frontier.size() - 1; i++) {
                for (int j = i + 1; j < frontier.size(); j++) {
                    if (frontier.get(j).containsAll(frontier.get(i))) {
                        frontier.remove(i);
                        i--;
                        break;
                    }
                }
            }

            for (int i = 0; i < frontier.size() - 1; i++) {
                for (int j = 0; j < result.size(); j++) {
                    if (frontier.get(j).containsAll(frontier.get(i))) {
                        frontier.remove(i);
                        i--;
                        break;
                    }
                }
            }

            for (int i = 0; i < frontier.size(); i++) {
                //FIXME: è orribile... functional?
                if (cards.indexOf(frontier.get(i).get(frontier.get(i).size() - 2)) < depth) {
                    result.add(frontier.remove(i));
                    i--;
                }
            }

            nodesInLastLevel = frontier.size();
            depth++;
        }

        return result.stream()
                .mapToInt(ArrayList::size)
                .max()
                .getAsInt();
    }
}
