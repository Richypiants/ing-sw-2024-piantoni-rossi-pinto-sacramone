package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

public class ResourcesObjectiveCard extends ObjectiveCard {
    public static final ArrayList<Resource> CONDITION = new ArrayList<>();

    public ResourcesObjectiveCard(int id, int points, Image frontSprite, Image backSprite, ArrayList<Resource> condition) {
        // Initialization logic goes here
    }

    public int calculatePoints() {
        // Implementation depends on specific resource conditions
        return 0; // Placeholder
    }
}
