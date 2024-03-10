package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.HashMap;

public class Field {
    public static final HashMap<Pair<Integer, Integer>, PlayableCard> FIELD = new HashMap<>();
    public static final ArrayList<Pair<Integer, Integer>> OPEN_CORNERS = new ArrayList<>();

    public Field() {
        // Field initialization logic
    }

    public void addCard(Pair<Integer, Integer> position, PlayableCard card) {
        FIELD.put(position, card);
        // Further logic to handle open corners if necessary
    }
}
