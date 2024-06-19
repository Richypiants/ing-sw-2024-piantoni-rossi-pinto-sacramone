package it.polimi.ingsw.gc12.Model.Server;

import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

/**
 * Represents a field that stores the cards placed by a player and manages the open corners where
 * the next cards can be played according to the rules
 */
public class Field {

    /**
     * The map from coordinates to the card played at that position, including the side it was played on.
     */
    private final LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> PLACED_CARDS;

    /**
     * The list of available positions where the next cards can be played.
     */
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;

    /**
     * Constructs an empty Field
     */
    protected Field() {
        this.PLACED_CARDS = new LinkedHashMap<>();
        this.OPEN_CORNERS = new ArrayList<>();
        OPEN_CORNERS.add(new GenericPair<>(0, 0));
    }

    /**
     * Adds a played card to the field at the specified coordinates and side.
     * If the action is successfully executed, updates the OPEN_CORNERS accordingly.
     * This method ensures that the corners involved in this operation are valid and that
     * the new placement does not overlap with other cards that are marked as
     * not having effective corners on that corner.
     *
     * @param coordinates The coordinates where the card is placed.
     * @param card        The card to be played.
     * @param playedSide  The side of the card that is facing up.
     * @throws InvalidCardPositionException if the coordinates are not valid for placing the card.
     */
    protected void addCard(GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
    throws InvalidCardPositionException {
        if (!OPEN_CORNERS.contains(coordinates)) {
            throw new InvalidCardPositionException();
        }

        PLACED_CARDS.put(coordinates, new GenericPair<>(card, playedSide));
        OPEN_CORNERS.remove(coordinates);

        //Finding and filtering the added card's corners to decide whether they have to be added to OPEN_CORNERS or not:
        card.getCorners(playedSide)
                .entrySet().stream()
                //Filtering out the NOT_A_CORNERs
                .filter((entry) -> !entry.getValue().equals(Resource.NOT_A_CORNER))
                //Checking whether the corner's neighbour coordinates aren't already in PLACED_CARDS of OPEN_CORNERS
                .map((entry) -> new GenericPair<>(
                                coordinates.getX() + entry.getKey().getX(),
                                coordinates.getY() + entry.getKey().getY()
                        )
                )
                .filter((openCorner) -> !(PLACED_CARDS.containsKey(openCorner) || OPEN_CORNERS.contains(openCorner)))
                //Checking whether the new slot can be added or if it does overlap a NOT_A_CORNER somewhere else:
                .filter((openCorner) -> card.getCorners(playedSide).keySet().stream()
                        .map((offset) ->
                                        //For every new corner, optionally get neighbouring placed cards, if there is any
                                Optional.ofNullable(
                                        PLACED_CARDS.get(
                                                new GenericPair<>(
                                                        openCorner.getX() + offset.getX(),
                                                        openCorner.getY() + offset.getY()
                                                )
                                        )
                                ).flatMap((optionalCard) -> //If there are neighbour cards, check that you do not cover eventual NOT_A_CORNERs
                                        Optional.of(
                                                !(optionalCard.getX()
                                                        .getCornerResource(
                                                                optionalCard.getY(),
                                                                -offset.getX(),
                                                                -offset.getY()
                                                        )
                                                        .equals(Resource.NOT_A_CORNER)
                                                )
                                        )
                                ).orElse(true)
                                //If there wasn't any neighbouring card, adding this corner is fine for the considered card
                        )
                        //Check that for any new corner this holds for ALL (eventual) neighbouring card
                        .reduce(true, (a, b) -> a && b)
                ).forEach(OPEN_CORNERS::add);

        //Remove the OPEN_CORNERS made inconsistent by the placed card
        for (GenericPair<Integer, Integer> corner : card.getCorners(playedSide).keySet()) {
            if (card.getCorners(playedSide).get(corner).equals(Resource.NOT_A_CORNER)) {
                OPEN_CORNERS.remove(
                        new GenericPair<>(coordinates.getX() + corner.getX(), coordinates.getY() + corner.getY())
                );
            }
        }
    }

    /**
     * Returns a copy of the map of placed cards, addressed by their position as key.
     *
     * @return A copy of the placed cards map.
     */
    public LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> getPlacedCards() {
        return new LinkedHashMap<>(PLACED_CARDS);
    }

    /**
     * Returns a copy of the list of corners where cards can be placed.
     *
     * @return A copy of the open corners list.
     */
    public ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return new ArrayList<>(OPEN_CORNERS);
    }

    /**
     * Returns the coordinates for a given card by navigating the map in the reversed way.
     *
     * @param placedCard The card for which to find the coordinates.
     * @return The coordinates of the given card.
     */
    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        return keyReverseLookup(PLACED_CARDS, (value) -> value.getX().equals(placedCard));
    }
}

