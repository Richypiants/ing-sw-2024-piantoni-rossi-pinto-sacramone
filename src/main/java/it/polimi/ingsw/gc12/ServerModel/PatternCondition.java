package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.disjoint;

//TODO: add documentation comments

public class PatternCondition implements PointsCondition {
    private ArrayList<Triplet<Integer, Integer, Resource>> condition;

    public PatternCondition(ArrayList<Triplet<Integer, Integer, Resource>> condition) {
        //TODO: should we keep safe copy of the condition arraylist?
        this.condition = new ArrayList<Triplet<Integer, Integer, Resource>>(condition);
    }

    protected ArrayList<Triplet<Integer, Integer, Resource>> getConditionParameters() {
        return new ArrayList<Triplet<Integer, Integer, Resource>>(condition);
    }

    //FIXME: ALL THIS CODE SHOULD BE CLEANED AND OPTIMIZED, IT IS TOO INTRICATE
    // AND PROBABLY REPEATS OPERATION AND IS NOT DRY
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //FIXME: sacra might be right, is it better to save all the cards of a pattern to avoid
        // propagating target? Or maybe should we add a map in the opposite direction?
        return largestMaximumCompatibilityClass(
                new ArrayList<PlayableCard>(target.getOwnField().entrySet().stream()
                        .filter((entry) -> !entry.getKey()
                                .equals(new GenericPair<Integer, Integer>(0, 0))
                        )
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
                ),
                target
        );
    }

    private int largestMaximumCompatibilityClass(ArrayList<PlayableCard> patternStartingCards,
                                                 InGamePlayer target) {
        ArrayList<ArrayList<PlayableCard>> frontier = new ArrayList<ArrayList<PlayableCard>>();
        ArrayList<ArrayList<PlayableCard>> result = new ArrayList<ArrayList<PlayableCard>>();
        int nodesInLastLevel = 1, depth = 0;

        frontier.add(patternStartingCards);

        while (!frontier.isEmpty()) {
            PlayableCard currentPattern = patternStartingCards.get(depth);

            for (int i = 0; i < nodesInLastLevel; i++) {
                ArrayList<PlayableCard> tmp = frontier.removeFirst();
                if (tmp.contains(currentPattern)) {
                    frontier.add(new ArrayList<PlayableCard>(tmp.subList(1, tmp.size())));
                    tmp.removeIf((pattern) -> !compatibleWith(pattern, currentPattern, target));
                }
                frontier.add(new ArrayList<PlayableCard>(tmp));
            }

            depth++; // We can put this here because we've already removed all nodes at the previous depth

            for (int i = 0; i < frontier.size() - 1; i++) {
                // We can start from i + 1, because all nodes on the right will contain a pattern which
                // has been previously discarded from this current branch
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
                //FIXME: it's really horrible... functional?
                if (patternStartingCards.indexOf(frontier.get(i).get(frontier.get(i).size() - 2)) < depth) {
                    result.add(frontier.remove(i));
                    i--;
                }
            }

            nodesInLastLevel = frontier.size();
        }

        return result.stream()
                .mapToInt(ArrayList::size)
                .max()
                .getAsInt();
    }

    private boolean compatibleWith(PlayableCard pattern1, PlayableCard pattern2, InGamePlayer target) {
        //FIXME: add try checks or exceptions?
        //FIXME: this isn't DRY, probably separate functions after cleaning up code?
        return disjoint(
                new ArrayList<GenericPair<Integer, Integer>>(
                        condition.stream()
                                .map((triplet) -> {
                                            GenericPair<Integer, Integer> thisPosition = target.getOwnField()
                                                    .entrySet().stream()
                                                    .filter((entry) -> entry.getValue().equals(pattern1))
                                                    .findFirst().get().getKey();
                                            return new GenericPair<Integer, Integer>(
                                                    thisPosition.getX() + triplet.getX(),
                                                    thisPosition.getY() + triplet.getY()
                                            );
                                        }
                                )
                                .collect(Collectors.toList())
                ),
                new ArrayList<GenericPair<Integer, Integer>>(
                        condition.stream()
                                .map((triplet) -> {
                                            GenericPair<Integer, Integer> thisPosition = target.getOwnField()
                                                    .entrySet().stream()
                                                    .filter((entry) -> entry.getValue().equals(pattern2))
                                                    .findFirst().get().getKey();
                                            return new GenericPair<Integer, Integer>(
                                                    thisPosition.getX() + triplet.getX(),
                                                    thisPosition.getY() + triplet.getY()
                                            );
                                        }
                                )
                                .collect(Collectors.toList())
                )
        );
    }
}
