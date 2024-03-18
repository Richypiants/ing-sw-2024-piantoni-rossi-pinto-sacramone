package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.ArrayList;
import java.util.HashMap;

/*
A field that stores the cards played by a player and the open corners where next cards can be played
 */
public class Field {

    /*
    The map from coordinates to the card played in that position
     */
    private final HashMap<GenericPair<Integer, Integer>, PlayableCard> PLACED_CARDS;

    /*
    Available position where the next cards can be played
     */
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;

    /*
    Constructs an empty Field
     */
    protected Field() {
        this.PLACED_CARDS = new HashMap<GenericPair<Integer, Integer>, PlayableCard>();
        this.OPEN_CORNERS = new ArrayList<GenericPair<Integer, Integer>>();
    }

    /*
    Adds a card to the played cards on the field
     */
    protected boolean addCard(GenericPair<Integer, Integer> coordinates, PlayableCard card) {
        if (!OPEN_CORNERS.contains(coordinates)) {
            return false;
        }

        //FIXME: if instanceof GoldCard check resourcesNeededToPlay
        PLACED_CARDS.put(coordinates, card);
        OPEN_CORNERS.remove(coordinates);

        for(int i = -1; i <= 1; i += 2){
            for(int j = -1; j <= 1; j += 2){
                GenericPair<Integer, Integer> newOpenCorner = new GenericPair<Integer, Integer>(
                        coordinates.getX() + i,
                        coordinates.getY() + j
                );
                if (!PLACED_CARDS.containsKey(newOpenCorner)){
                    OPEN_CORNERS.add(newOpenCorner);
                }
            }
        }

        return true;
    }

    /*
    Returns a copy of the map of placed cards, addressed by their position as key
     */
    public HashMap<GenericPair<Integer, Integer>, PlayableCard> getPlacedCards() {
        return new HashMap<GenericPair<Integer, Integer>, PlayableCard>(PLACED_CARDS);
    }

    /*
    Returns a copy of the list of corners where cards can be placed
     */
    public ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return new ArrayList<GenericPair<Integer, Integer>>(OPEN_CORNERS);
    }

    /*
    Returns the coordinates for a given card, inverting the map
     */
    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        //TODO: check for exceptions and Optional<> value when card hasn't been played!
        return PLACED_CARDS.entrySet().stream()
                .filter((entry) -> entry.getValue().equals(placedCard))
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
