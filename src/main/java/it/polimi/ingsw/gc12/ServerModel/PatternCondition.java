package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.disjoint;

/*
A condition that counts how many "disjoint" times the condition pattern can be found among the played cards
 */
public class PatternCondition implements PointsCondition {

    /*
    The pattern to be found among the played cards
     */
    private final ArrayList<Triplet<Integer, Integer, Resource>> CONDITION;

    public PatternCondition(ArrayList<Triplet<Integer, Integer, Resource>> condition) {
        //TODO: should we keep safe copy of the condition arraylist?
        this.CONDITION = new ArrayList<Triplet<Integer, Integer, Resource>>(condition);
    }

    /*
    Returns a copy of this card's condition pattern
     */
    //FIXME: make this collection immutable (all the other ones in PointsCondition subclasses too?
    // somewhere else too?)
    protected ArrayList<Triplet<Integer, Integer, Resource>> getConditionParameters() {
        return new ArrayList<Triplet<Integer, Integer, Resource>>(CONDITION);
    }

    /*
    Counts how many corners are covered when playing the associated card.
    The same-type patterns should be considered in a way such that the points obtained from them is maxed.
    Thus, we want to find the largest maximum compatibility class between all the same-type patterns,
    that is the choice of patterns such that we consider the most possible amount of them
     */
    //FIXME: ALL THIS CODE SHOULD BE CLEANED AND OPTIMIZED, IT IS TOO INTRICATE
    // AND PROBABLY REPEATS OPERATION AND IS NOT DRY
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //FIXME: sacra might be right, is it better to save all the cards of a pattern to avoid
        // propagating target? Or maybe should we add a map in the opposite direction?
        return largestMaximumCompatibilityClass(
                target.getPlacedCards().entrySet().stream()
                        // We don't want to consider the initial card
                        .filter((entry) -> !entry.getKey()
                                .equals(new GenericPair<Integer, Integer>(0, 0))
                        )
                        // We only keep the cards which are the same type of the start of the considered pattern
                        .filter((entry) -> entry.getValue()
                                .getCenterBackResources().getFirst()
                                .equals(CONDITION.getFirst().getZ())
                        )
                        // We only keep the ones that actually form the pattern
                        //FIXME: can we merge this filter and the one above in a single one?
                        .filter((entry) -> getConditionParameters().subList(1, CONDITION.size()).stream()
                                .map((offset) -> target.getPlacedCards().get(
                                        new GenericPair<Integer, Integer>(
                                                entry.getKey().getX() + offset.getX(),
                                                entry.getKey().getY() + offset.getY()
                                        )
                                                )
                                                .getCenterBackResources().getFirst()
                                                .equals(offset.getZ())
                                )
                                .reduce(true,
                                        (accumulator, isColorCorrect) -> accumulator && isColorCorrect
                                )
                        )
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toCollection(ArrayList::new)),
                target.getOwnField()
        );
    }

    /*
    Using the tree algorithm from Digital Circuits Design course, finds the size of the largest compatibility
    class, that is the maximum number of patterns that are disjoint
     */
    private int largestMaximumCompatibilityClass(ArrayList<PlayableCard> patternStartingCards,
                                                 Field playerField) {
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
                    tmp.removeIf((pattern) -> !compatibleWith(pattern, currentPattern, playerField));
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

        //FIXME: add Optional<> managmement and eventual exceptions?
        return result.stream()
                .mapToInt(ArrayList::size)
                .max()
                .getAsInt();
    }

    /*
    Checks whether the patterns passed as parameters are compatible (disjoint)
     */
    //FIXME: maybe a BiMap would solve this problem?
    private boolean compatibleWith(PlayableCard pattern1, PlayableCard pattern2, Field playerField) {
        //FIXME: add try checks or exceptions?
        return disjoint(
                fullPatternCoordinates(pattern1, playerField),
                fullPatternCoordinates(pattern2, playerField)
        );
    }

    /*
    Returns the list of coordinates of all the cards in the pattern passed as parameter
     */
    private ArrayList<GenericPair<Integer, Integer>> fullPatternCoordinates(
            PlayableCard pattern, Field playerField) {
        return CONDITION.stream()
                .map((triplet) -> {
                    GenericPair<Integer, Integer> thisPosition = playerField.getCardCoordinates(pattern);
                            return new GenericPair<Integer, Integer>(
                                    thisPosition.getX() + triplet.getX(),
                                    thisPosition.getY() + triplet.getY()
                            );
                        }
                ).collect(Collectors.toCollection(ArrayList::new));
    }
}

// Test
// - Casi limite
//   thisCard undefined
//   target undefined
//
//   Zero tiles on field
//   One tile on field
//   More than one tiles on field
//   No pattern

