package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: complete from UML and add comments for documentation

public class InGamePlayer extends Player {
    public static final Color COLOR = null; // Placeholder for Color
    private int points;
    private ArrayList<PlayableCard> cardsInHand;
    private HashMap<Resource, Integer> ownedResources;
    private Field ownField;
    private ObjectiveCard secretObjective;


    //Constructor method
    protected InGamePlayer(String nickname) {
        super(nickname);
        cardsInHand = new ArrayList<>();
        ownedResources = new HashMap<>();
        secretObjective = null;
        ownField = new Field();
    }

    //Given the desired amount to be increased by, updates the player's points
    protected void increasePoints(int pointsToAdd){
        points += pointsToAdd;
    }

    //Getter points method
    protected int getPoints(){
        return points;
    }

    //Getter cardsInHand method returned by copy
    protected ArrayList<PlayableCard> getCardsInHand(){
        return new ArrayList<PlayableCard>(cardsInHand);
    }

    //Given the card and the desired position,
    // wrapped in a GenericPair structure meaning <x,y> coordinates on the field,
    // places the card into the ownField HashMap
    //FIXME: DONE? fix uml parameter card in playable card
    protected void placeCard(PlayableCard card, GenericPair<Integer, Integer> pair) {
        ownField.addCard(pair, card);
    }

    //Adds the pickedCard to the current player's hand
    protected void addCardToHand(PlayableCard pickedCard){
        //FIXME: check for exception!
        cardsInHand.add(pickedCard);
    }

    //Given a specific resource type and the quantity to be increased by, updates the HashMap
    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
        ownedResources.put(resource, ownedResources.get(resource) + numberToBeIncreased);
    }

    //Getter ownedResource method returned by copy
    protected HashMap<Resource, Integer> getOwnedResources() {
        return new HashMap<Resource, Integer>(ownedResources);
    }

    //Getter ownField method relying on Field class
    protected Field getOwnField() {
        //FIXME: avoid reference escaping?
        return this.ownField;
    }

    protected HashMap<GenericPair<Integer, Integer>, PlayableCard> getPlacedCards() {
        return ownField.getPlacedCards();
    }

    protected ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return ownField.getOpenCorners();
    }

    //Setter secretObjective method assigned directly
    protected void setSecretObjective(ObjectiveCard objectiveCard){
        //FIXME: directly assigned from the parameter, but the getter method is Safe, so should be good.
        this.secretObjective = objectiveCard;
    }

    //Getter secretObjective method returned by copy
    protected ObjectiveCard getSecretObjective(){
        return new ObjectiveCard( this.secretObjective );
    }

}
