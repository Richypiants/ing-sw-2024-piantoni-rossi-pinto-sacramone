package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * A field that stores the cards played by a player and the open corners where the next cards can be played
 */
public class Field {

    /**
     * The map from coordinates to the card played in that position
     */
    private final HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> PLACED_CARDS;

    /**
     * Available position where the next cards can be played
     */
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;

    /**
     * Constructs an empty Field
     */
    protected Field() {
        this.PLACED_CARDS = new HashMap<>();
        this.OPEN_CORNERS = new ArrayList<>();
        OPEN_CORNERS.add(new GenericPair<>(0, 0));
    }

    /**
     * Adds a card to the played cards on the field
     * @requires the given coordinates' pair is valid (contained in OPEN_CORNERS)
     * @ensures the given card is now contained in PLAYED_CARDS
     * @ensures the given coordinates are no longer in OPEN_CORNERS
     * @ensures the coordinates' pairs corresponding to the given card's corners are added to OPEN_CORNERS, but only if:
     * - 1) they really are corners (they do not contain NOT_A_CORNER);
     * - 2) there isn't a card already placed at those coordinates in PLACED_CARDS
     * - 3) they are not already contained in OPEN_CORNERS
     * - 4) they do not overlap a NOT_A_CORNER of another card in PLACED_CARDS
     */
    protected boolean addCard(GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide) {
        if (!OPEN_CORNERS.contains(coordinates)) {
            return false;
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

        return true;
    }

    /**
     * Returns a copy of the map of placed cards, addressed by their position as key
     */
    public HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> getPlacedCards() {
        return new HashMap<>(PLACED_CARDS);
    }

    /**
     * Returns a copy of the list of corners where cards can be placed
     */
    public ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return new ArrayList<>(OPEN_CORNERS);
    }

    /**
     * Returns the coordinates for a given card, inverting the map
     */
    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        //TODO: check for exceptions and Optional<> value when card hasn't been played!
        // look in leaveLobbby() in Controller for a solution to a similar problem to this get()
        return PLACED_CARDS.entrySet().stream()
                .filter((entry) -> entry.getValue().getX().equals(placedCard))
                .findFirst().get().getKey();
    }

}

// addCard() -> Si test
//              - Statement coverage (edge coverage)
//                OPEN_CORNERS.contains(coordinates) = TRUE
//                OPEN_CORNERS.contains(coordinates) = FALSE
//
//                PLACED_CARDS.containsKey(newOpenCorner) = TRUE
//                PLACED_CARDS.containsKey(newOpenCorner) = FALSE
//
//              - Casi limite
//                card undefined
//
// getPlacedCards() (Getter) -> No test
// getOpenCorners() (Getter) -> No test
// getCardCoordinates() (Getter) -> Si test
//                                  - Casi limite
//                                    placedCard undefined
