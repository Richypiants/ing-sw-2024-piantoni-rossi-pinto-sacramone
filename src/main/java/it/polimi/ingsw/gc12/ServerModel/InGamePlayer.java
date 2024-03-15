package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: complete from UML and add comments for documentation

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

    //FIXME: add parameter to uml , fix uml parameter card in playable card
    protected void placeCard(PlayableCard card, GenericPair<Integer, Integer> pair) {
        ownField.addCard(pair, card);
    }

    protected void addCardToHand(PlayableCard pickedCard){
        //FIXME: check for exception!
        cardsInHand.add(pickedCard);
    }

    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
        ownedResources.put(resource, ownedResources.get(resource) + numberToBeIncreased);
    }

    protected HashMap<Resource, Integer> getOwnedResourced(){
        return new HashMap<Resource, Integer>(ownedResources);
    }

    protected HashMap<GenericPair<Integer, Integer>, PlayableCard> getOwnField() {
        return ownField.getField();
    }

    protected void setSecretObjective(ObjectiveCard objectiveCard){
    }

    protected ObjectiveCard getSecretObjective(){
        return null;
    }

}
