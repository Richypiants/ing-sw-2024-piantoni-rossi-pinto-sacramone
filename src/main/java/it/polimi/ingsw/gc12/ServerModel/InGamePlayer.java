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
    private final ArrayList<PlayableCard> CARDS_IN_HAND;
    private final HashMap<Resource, Integer> OWNED_RESOURCES;
    private final Field OWN_FIELD;
    private ObjectiveCard secretObjective;


    //Constructor method
    protected InGamePlayer(Player player) {
        super(player);
        CARDS_IN_HAND = new ArrayList<>();
        OWNED_RESOURCES = new HashMap<>();
        secretObjective = null;
        OWN_FIELD = new Field();
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
        return new ArrayList<PlayableCard>(CARDS_IN_HAND);
    }

    // Given the card and the desired position,
    // wrapped in a GenericPair structure meaning <x,y> coordinates on the field,
    // places the card into the ownField HashMap
    protected void placeCard(PlayableCard card, GenericPair<Integer, Integer> pair) {
        OWN_FIELD.addCard(pair, card);
    }

    //Adds the pickedCard to the current player's hand
    protected void addCardToHand(PlayableCard pickedCard){
        //FIXME: check for exception!
        CARDS_IN_HAND.add(pickedCard);
    }

    // Given a specific resource type and the quantity to be increased by, updates the HashMap
    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
        OWNED_RESOURCES.put(resource, OWNED_RESOURCES.get(resource) + numberToBeIncreased);
    }

    //Getter ownedResource method returned by copy
    protected HashMap<Resource, Integer> getOwnedResources() {
        return new HashMap<Resource, Integer>(OWNED_RESOURCES);
    }

    //Getter ownField method relying on Field class
    protected Field getOwnField() {
        //FIXME: avoid reference escaping?
        return this.OWN_FIELD;
    }

    protected HashMap<GenericPair<Integer, Integer>, PlayableCard> getPlacedCards() {
        return OWN_FIELD.getPlacedCards();
    }

    protected ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return OWN_FIELD.getOpenCorners();
    }

    //Setter secretObjective method assigned directly
    protected void setSecretObjective(ObjectiveCard objectiveCard){
        //FIXME: directly assigned from the parameter, but the getter method is Safe, so should be good.
        this.secretObjective = objectiveCard;
    }

    //Getter secretObjective method returned by copy
    protected ObjectiveCard getSecretObjective(){
        return new ObjectiveCard(this.secretObjective);
    }

}

// increasePoints() -> No test
// getPoints() (Getter) -> No test
// getCardsInHand() (Getter) -> No test
// placeCard() -> Si test
//                - Casi limite
//                  card undefined
//
// addCardToHand() -> Si test
//                    - Casi limite
//                      pickedCard undefined
//
// incrementOwnedResource() -> No test
// getOwnedResources() (Getter) -> No test
// getOwnField() (Getter) -> No test
// getPlacedCards() (Getter) -> No test
// getOpenCorners() (Getter) -> No test
// setSecretObjective() (Setter senza condizioni particolari) -> Si test
//                                                               - Casi limite
//                                                                 objectiveCard undefined
//
// getSecretObjective() (Getter) -> No test
