package it.polimi.ingsw.gc12.Model.Server.Conditions;

import it.polimi.ingsw.gc12.Model.Server.Cards.Card;
import it.polimi.ingsw.gc12.Model.Server.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.disjoint;

/**
 * A condition that counts how many "disjoint" times the condition pattern can be found among the played cards.
 * This condition is used to evaluate specific patterns formed by the type of resources related the played cards.
 * The patterns must be disjoint, meaning they do not share any overlapping parts, to be counted.
 * </p>
 */
public class PatternCondition implements PointsCondition {

    /**
     * The pattern to be found on the field among the played cards.
     * This list of {@link Triplet} represents the required pattern of resources, where each triplet
     * contains the x and y offsets indicating the corners and the {@link Resource} type.
     * </p>
     */
    private final List<Triplet<Integer, Integer, Resource>> CONDITION;

    /**
     * Generates an instance of a pattern condition from the given condition in parameters.
     *
     * @param condition The pattern to be found among the played cards.
     */
    public PatternCondition(List<Triplet<Integer, Integer, Resource>> condition) {
        this.CONDITION = List.copyOf(condition);
    }

    /**
     * Returns this card's condition pattern.
     *
     * @return A list of {@link Triplet} representing the condition pattern.
     */
    protected List<Triplet<Integer, Integer, Resource>> getConditionParameters() {
        return CONDITION;
    }

    /**
     * Counts how many times the condition pattern is satisfied when playing the associated card.
     * The same-type patterns should be considered in a way that maximizes the points obtained from them.
     * This method finds the largest set of disjoint patterns among the played cards.
     *
     * @param thisCard The card being played.
     * @param target The player who is playing the card.
     * @return The number of times the pattern condition is satisfied.
     */
    //FIXME: ALL THIS CODE SHOULD BE CLEANED AND OPTIMIZED, IT IS TOO INTRICATE
    // AND PROBABLY REPEATS OPERATION AND IS NOT DRY
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        return largestMaximumCompatibilityClass(
                target.getPlacedCards().entrySet().stream()
                        // We don't want to consider the initial card
                        .filter((entry) -> !(entry.getValue().getX() instanceof InitialCard))
                        // We only want to keep the cards that actually make a valid pattern, and we only keep
                        // the starting one for each pattern
                        .filter((entry) -> getConditionParameters().stream()
                                .map((triplet) ->
                                        Optional.ofNullable(target.getPlacedCards().get(
                                                new GenericPair<>(
                                                        entry.getKey().getX() + triplet.getX(),
                                                        entry.getKey().getY() + triplet.getY()
                                                )
                                                )
                                        ).flatMap((cardPair) ->
                                                Optional.of(cardPair.getX()
                                                        .getCenterBackResources()
                                                        .containsKey(triplet.getZ())
                                                )
                                        ).orElse(false)
                                )
                                .reduce(true,
                                        (accumulator, isColorCorrect) -> accumulator && isColorCorrect
                                )
                        ).map((entry) -> entry.getValue().getX())
                        .collect(Collectors.toCollection(ArrayList::new)),
                target
        );
    }

    /**
     * Finds the size of the largest compatibility class using a tree algorithm,
     * determining the maximum number of disjoint patterns of the same type.
     * This method evaluates all potential starting cards for the patterns and
     * identifies the largest set of patterns that do not overlap.
     *
     * @param patternStartingCards An {@link ArrayList} containing all candidate cards that can form a valid pattern.
     *                             Each pattern is represented by one of its constituent cards.
     * @param target The {@link InGamePlayer} whose field will be searched for valid patterns.
     * @return The size of the largest compatibility class of disjoint patterns.
     */
    private int largestMaximumCompatibilityClass(ArrayList<PlayableCard> patternStartingCards,
                                                 InGamePlayer target) {
        ArrayList<ArrayList<PlayableCard>> frontier = new ArrayList<>();
        ArrayList<ArrayList<PlayableCard>> result = new ArrayList<>();
        int nodesInLastLevel = 1, depth = 0;

        if (patternStartingCards.isEmpty()) return 0;

        frontier.add(new ArrayList<>(patternStartingCards));

        while (!frontier.isEmpty()) {
            PlayableCard currentPattern = patternStartingCards.get(depth);

            for (int i = 0; i < nodesInLastLevel; i++) {
                ArrayList<PlayableCard> tmp = frontier.removeFirst();
                if (tmp.contains(currentPattern)) {
                    frontier.add(new ArrayList<>(tmp.subList(1, tmp.size())));
                    tmp.removeIf((pattern) -> !pattern.equals(currentPattern) &&
                            !compatibleWith(pattern, currentPattern, target)
                    );
                }
                frontier.add(new ArrayList<>(tmp));
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
                if (frontier.get(i).size() < 2 || patternStartingCards.indexOf(frontier.get(i).get(frontier.get(i).size() - 2)) < depth) {
                    result.add(frontier.remove(i));
                    i--;
                }
            }

            nodesInLastLevel = frontier.size();
        }

        return result.stream()
                .mapToInt(ArrayList::size)
                .max()
                .orElseThrow();
    }

    /**
     * Checks whether the patterns passed as parameters are compatible (disjoint).
     *
     * @param pattern1 The first pattern to be checked.
     * @param pattern2 The second pattern to be checked.
     * @param target The player whose cards are being checked.
     * @return {@code true} if the patterns are disjoint, {@code false} otherwise.
     */
    private boolean compatibleWith(PlayableCard pattern1, PlayableCard pattern2, InGamePlayer target) {
        return disjoint(
                fullPatternCoordinates(pattern1, target),
                fullPatternCoordinates(pattern2, target)
        );
    }

    /**
     * Returns the list of coordinates of all the cards in the pattern passed as parameter.
     *
     * @param pattern The pattern to get the coordinates for.
     * @param target The player whose cards are being checked.
     * @return A list of {@link GenericPair} representing the coordinates of the pattern.
     */
    private List<GenericPair<Integer, Integer>> fullPatternCoordinates(
            PlayableCard pattern, InGamePlayer target) {
        return CONDITION.stream()
                .map((triplet) -> {
                    GenericPair<Integer, Integer> thisPosition = target.getCardCoordinates(pattern);
                    return new GenericPair<>(
                                    thisPosition.getX() + triplet.getX(),
                                    thisPosition.getY() + triplet.getY()
                            );
                        }
                ).toList();
    }

    /**
     * Returns a string representation of this condition.
     * The string representation provides a simple description indicating that this
     * is a PatternCondition along with the condition pattern.
     *
     * @return A string representation of this condition.
     */
    @Override
    public String toString() {
        return "(PatternCondition) {" +
                "CONDITION=" + CONDITION +
                "} ";
    }
}

