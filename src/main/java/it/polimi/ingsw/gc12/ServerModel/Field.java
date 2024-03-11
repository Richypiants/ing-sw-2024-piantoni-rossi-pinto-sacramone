package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.HashMap;

// This class implements a generic field structure to store cards played from a player
public class Field {
    // Collection to store pairs of coordinates as keys to the card placed in that position
    private HashMap<Pair<Integer, Integer>, PlayableCard> field = new HashMap<Pair<Integer, Integer>, PlayableCard>();
    // Collection to store coordinates that correspond to available moves, that are free corners on the field
    private ArrayList<Pair<Integer, Integer>> openCorners = new ArrayList<Pair<Integer, Integer>>();

    // Constructor for a field that initializes it
    protected Field() {
        //TODO: Field initialization logic
    }

    // Interface method for external callers to place a card on the field
    protected boolean addCard(Pair<Integer, Integer> coordinates, PlayableCard card) {
        //TODO: verify open corners or delegate control to the Controller? (void or boolean?)
        // Also, should the pair be sanitized?
        field.put(coordinates, card);
        return true;
    }

    // Getter method for open corners where a card can be placed
    public ArrayList<Pair<Integer, Integer>> getOpenCorners() {
        return new ArrayList<Pair<Integer, Integer>>(openCorners);
    }


}
