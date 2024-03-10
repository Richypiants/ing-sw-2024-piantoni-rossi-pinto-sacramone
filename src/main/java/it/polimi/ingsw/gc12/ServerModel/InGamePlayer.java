package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.HashMap;

public class InGamePlayer extends Player {
    public static final Color COLOR = null; // Placeholder for ENUM_Color
    private int points;
    private ArrayList<PlayableCard> cardsInHand;
    private HashMap<Resource, Integer> ownedResources;
    private Field ownField = null; // Placeholder
    private ObjectiveCard secretObjective = null; // Placeholder

    protected InGamePlayer(String nickname) {
        super(nickname);
        cardsInHand = new ArrayList<>();
        ownedResources = new HashMap<>();
    }

    protected void increasePoints(int pointsToAdd){
        points += pointsToAdd;
    }

    protected int getPoints(){
        return points;
    }

    protected ArrayList<PlayableCard> getCardsInHand(){
        return new ArrayList<PlayableCard>( cardsInHand);
    }

    protected void placeCard(Card card) {
        // Implementation depends on game logic
    }

    protected void addCardToHand(PlayableCard pickedCard){
    }

    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
    }

    protected HashMap<Resource, Integer> getOwnedResourced(){
        return new HashMap<Resource, Integer>(ownedResources);
    }

    protected Field getOwnField(){
        return new Field(ownField);
    }

    protected void setSecretObjective(ObjectiveCard objectiveCard){
    }

    protected ObjectiveCard getSecretObjective(){
    }

}
