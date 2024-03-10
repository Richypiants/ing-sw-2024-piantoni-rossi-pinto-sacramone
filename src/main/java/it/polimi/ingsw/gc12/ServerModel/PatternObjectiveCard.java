package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

public class PatternObjectiveCard extends ObjectiveCard {
    public static final ArrayList<Triplet<Integer, Integer, Resource>> CONDITION = new ArrayList<>();

    public PatternObjectiveCard(int id, int points, Image frontSprite, Image backSprite, ArrayList<Triplet<Integer, Integer, Resource>> condition) {
        // Initialization logic goes here
    }

    public int calculatePoints() {
        // Implementation depends on specific pattern conditions
        return 0; // Placeholder
    }
}
