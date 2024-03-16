package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.ArrayList;
import java.util.HashMap;

// This class implements a generic field structure to store cards played from a player
public class Field {
    // Collection to store pairs of coordinates as keys to the card placed in that position
    private final HashMap<GenericPair<Integer, Integer>, PlayableCard> PLACED_CARDS;
    // Collection to store coordinates that correspond to available moves, that are free corners on the field
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;

    // Constructor for a field that initializes it
    protected Field() {
        //TODO: Field initialization logic
        this.PLACED_CARDS = new HashMap<GenericPair<Integer, Integer>, PlayableCard>();
        this.OPEN_CORNERS = new ArrayList<GenericPair<Integer, Integer>>();
    }

    // Interface method for external callers to place a card on the field
    protected boolean addCard(GenericPair<Integer, Integer> coordinates, PlayableCard card) {
        if(!OPEN_CORNERS.contains(coordinates))
            return false;

        PLACED_CARDS.put(coordinates, card);
        OPEN_CORNERS.remove( coordinates);

        for(int i = -1; i <= 1; i += 2){
            for(int j = -1; j <= 1; j += 2){
                GenericPair<Integer, Integer> newOpenCorner = new GenericPair<Integer, Integer>( coordinates.getX() + i, coordinates.getY() + j);
                if (!PLACED_CARDS.containsKey(newOpenCorner)){
                    OPEN_CORNERS.add(newOpenCorner);
                }
            }
        }

        return true;
    }



    // Getter method for already placed cards FIXME: rename method to getPlacedCards!
    public HashMap<GenericPair<Integer, Integer>, PlayableCard> getPlacedCards() {
        return new HashMap<GenericPair<Integer, Integer>, PlayableCard>(PLACED_CARDS);
    }

    // Getter method for open corners where a card can be placed
    public ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return new ArrayList<GenericPair<Integer, Integer>>(OPEN_CORNERS);
    }

    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        return PLACED_CARDS.entrySet().stream()
                .filter((entry) -> entry.getValue().equals(placedCard))
                .findFirst().get().getKey();
    }

}
