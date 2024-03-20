package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.HashMap;

/*
A field that stores the cards played by a player and the open corners where next cards can be played
 */
public class Field {

    /*
    The map from coordinates to the card played in that position
     */
    private final HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> PLACED_CARDS;

    /*
    Available position where the next cards can be played
     */
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;

    /*
    Constructs an empty Field
     */
    protected Field() {
        this.PLACED_CARDS = new HashMap<>();
        this.OPEN_CORNERS = new ArrayList<>();
        OPEN_CORNERS.add(new GenericPair<>(0, 0));
    }

    /*
    Adds a card to the played cards on the field
     */
    protected boolean addCard(GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide) {
        if (!OPEN_CORNERS.contains(coordinates)) {
            return false;
        }

        PLACED_CARDS.put(coordinates, new GenericPair<>(card, playedSide));
        OPEN_CORNERS.remove(coordinates);

        //FIXME: maybe having corners = Resource[][][] might help? finding f(i) and g(i)?
        // change mapping coordinates function...?
        for (int i = 0; i < 4; i++) {
            if (!card.getCornerResource(i, playedSide).equals(Resource.NOT_A_CORNER)) {
                GenericPair<Integer, Integer> newOpenCorner = new GenericPair<>(
                        coordinates.getX() + (i / 2) * 2 - 1, //TODO: f(i)
                        coordinates.getY() + (i % 2) * 2 - 1 //TODO: g(i)
                );

                if (!(PLACED_CARDS.containsKey(newOpenCorner) || OPEN_CORNERS.contains(newOpenCorner))) {
                    for (int row = -1; row <= 1; row += 2) {
                        for (int column = -1; column <= 1; column += 2) {
                            GenericPair<PlayableCard, Side> coveredCard = PLACED_CARDS.get(
                                    new GenericPair<>(
                                            coordinates.getX() + column,
                                            coordinates.getY() + row
                                    )
                            );

                            if (coveredCard.getX()
                                    .getCornerResource((-1 * row + 1) + (-1 * column + 1) / 2,
                                            coveredCard.getY()
                                    ).equals(Resource.NOT_A_CORNER)
                            ) continue; //TODO: implement skip addCorner
                        }
                    }
                    OPEN_CORNERS.add(newOpenCorner);
                }
            }
        }

        return true;
    }

    /*
    Returns a copy of the map of placed cards, addressed by their position as key
     */
    public HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> getPlacedCards() {
        return new HashMap<>(PLACED_CARDS);
    }

    /*
    Returns a copy of the list of corners where cards can be placed
     */
    public ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return new ArrayList<>(OPEN_CORNERS);
    }

    /*
    Returns the coordinates for a given card, inverting the map
     */
    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        //TODO: check for exceptions and Optional<> value when card hasn't been played!
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
