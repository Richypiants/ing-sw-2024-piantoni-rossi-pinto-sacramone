package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

public class ResourcesGoldCard extends GoldCard {
    private ArrayList<Resource> condition = new ArrayList<Resource>();

    protected ResourcesGoldCard(int id, int points, Image frontSprite, Image backSprite, Resource[] cornerResources, boolean[] cornerValidity, ArrayList<Resource> neededResources, ArrayList<Resource> loadedCondition) {
        super(id, frontSprite, backSprite, points, cornerResources, cornerValidity, neededResources);
        condition.addAll(loadedCondition);
    }

    @Override
    protected int calculatePoints() {
        // Implementation for calculating points based on resources condition
        return 0; // Placeholder
    }

    protected ArrayList<Resource> getCondition(){
        return new ArrayList<Resource>(condition);
    }


}
