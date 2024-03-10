package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

public class CornersGoldCard extends GoldCard {
    public CornersGoldCard(int id, int points, Image frontSprite, Image backSprite, Resource[] cornerResources, boolean[] cornerValidity, ArrayList<Resource> neededResources) {
        super(id, frontSprite, backSprite, points, cornerResources, cornerValidity, neededResources);
        // Further initialization if necessary
    }

    @Override
    public int calculatePoints() {
        // Implementation for calculating points based on corners
        return 0; // Placeholder
    }
}
