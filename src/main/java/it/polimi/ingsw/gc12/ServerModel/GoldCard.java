package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

public class GoldCard extends PlayableCard {
    public static final ArrayList<Resource> NEEDED_RESOURCES_TO_PLAY = new ArrayList<>();

    public GoldCard(int id, Image frontSprite, Image backSprite, int points, Resource[] cornerResources, boolean[] cornerValidity, ArrayList<Resource> neededResources) {
        super(id, points, frontSprite, backSprite, cornerResources, cornerValidity);
        NEEDED_RESOURCES_TO_PLAY.addAll(neededResources);
    }

    public ArrayList<Resource> getNeededResourcesToPlay() {
        return new ArrayList<>(NEEDED_RESOURCES_TO_PLAY);
    }
}
