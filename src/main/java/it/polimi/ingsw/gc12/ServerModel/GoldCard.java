package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

//TODO: complete from UML and add comments for documentation

public class GoldCard extends PlayableCard {
    public PointsCondition pointsCondition;
    public ResourcesCondition resourcesNeededToPlay;


    // Constructor for generating GoldCards, expected parameters retrieved from the JSON Card File.
    public GoldCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                    ArrayList<Resource> centerBackResources, PointsCondition pointsCondition,
                    ResourcesCondition resourcesNeededToPlay) {
        super(id, pointsGranted, frontSprite, backSprite, corners, centerBackResources);
        this.resourcesNeededToPlay = resourcesNeededToPlay;
    }

    // Getter method for neededResourcesToPlay
    public ArrayList<Resource> getNeededResourcesToPlay() {
        return resourcesNeededToPlay.getConditionParameters();
    }

    // Returns the number of points the target InGamePlayer is awarded from playing the specific Gold Card,
    // satisfying its specified condition
    @Override
    public int awardPoints(InGamePlayer target){
        return POINTS_GRANTED * pointsCondition.numberOfTimesSatisfied(target);

    }
}
