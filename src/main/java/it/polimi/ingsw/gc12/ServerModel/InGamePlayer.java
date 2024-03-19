package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.stream.Collectors;

/*
A structure for a player which is currently playing a game
 */
public class InGamePlayer extends Player {

    /*
    This player's color
     */
    public static final Color COLOR = null; //TODO: implement color selection logic
    /*
    The cards in this player's hand
     */
    private final ArrayList<PlayableCard> CARDS_IN_HAND;
    /*
    The resources owned by this player currently
     */
    private final EnumMap<Resource, Integer> OWNED_RESOURCES;
    /*
    The field of this player
     */
    private final Field OWN_FIELD;
    /*
    The points currently gained by this player
     */
    private int points;
    /*
    The secret Objective Card chosen by this player
     */
    private ObjectiveCard secretObjective;


    /*
    Constructs an InGamePlayer from the player passed as parameter
     */
    protected InGamePlayer(Player player) {
        super(player);
        CARDS_IN_HAND = new ArrayList<>();
        OWNED_RESOURCES = new EnumMap<>(Resource.class);
        for (Resource r : Arrays.stream(Resource.values())
                .filter((resource) -> !(resource == Resource.EMPTY || resource == Resource.NOT_A_CORNER))
                .collect(Collectors.toCollection(ArrayList::new))
        ) {
            OWNED_RESOURCES.put(r, 0);
        }
        secretObjective = null;
        OWN_FIELD = new Field();
    }

    /*
    Given the desired amount to be increased by, updates the player's points
     */
    protected void increasePoints(int pointsToAdd){
        points += pointsToAdd;
    }

    /*
    Returns this player's current points
     */
    protected int getPoints(){
        return points;
    }

    /*
    Returns a copy of the list of cards in this player's hand
     */
    protected ArrayList<PlayableCard> getCardsInHand(){
        return new ArrayList<>(CARDS_IN_HAND);
    }

    /* Given the card and the desired position, wrapped in a GenericPair structure meaning <x,y> coordinates on
     the field, places the card into the ownField HashMap, also incrementing the ownedResources by the correct number
     */
    protected boolean placeCard(PlayableCard card, Side playedSide, GenericPair<Integer, Integer> pair) {
        if (card instanceof GoldCard)
            if (((GoldCard) card).getNeededResourcesToPlay().numberOfTimesSatisfied(card, this) <= 0)
                return false;

        if (OWN_FIELD.addCard(pair, card, playedSide)) {
            if (playedSide == Side.FRONT) {
                for (Resource r : card.getCorners(playedSide)) {
                    incrementOwnedResource(r, 1);
                }
            } else {
                card.getCenterBackResources()
                        .forEach(this::incrementOwnedResource);
            }
        }
        return true;
    }
    /*
    Adds the pickedCard to the current player's hand
     */
    protected void addCardToHand(PlayableCard pickedCard){
        //FIXME: check for exception!
        CARDS_IN_HAND.add(pickedCard);
    }

    /*
    Given a specific resource type and the quantity to be increased by, updates the HashMap
     */
    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
        OWNED_RESOURCES.put(resource, OWNED_RESOURCES.get(resource) + numberToBeIncreased);
    }

    /*
    Returns a copy of the map of resources owned by this player
     */
    protected EnumMap<Resource, Integer> getOwnedResources() {
        return new EnumMap<>(OWNED_RESOURCES);
    }

    /*
    Returns this player's own field
     */
    protected Field getOwnField() {
        //FIXME: avoid reference escaping?
        return this.OWN_FIELD;
    }

    /*
    Returns the cards placed by this player
     */
    //FIXME: having method calls like these is strange, maybe ask the teacher?
    protected HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> getPlacedCards() {
        return OWN_FIELD.getPlacedCards();
    }

    /*
    Returns the available positions where this player can place cards next
     */
    protected ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return OWN_FIELD.getOpenCorners();
    }

    /*
    Returns this player's secret Objective card
     */
    protected ObjectiveCard getSecretObjective(){
        return secretObjective;
    }

    /*
    Sets this player's secret Objective card
     */
    protected void setSecretObjective(ObjectiveCard objectiveCard){
        //FIXME: if Cards classes' attributes are final, this is fine.
        this.secretObjective = objectiveCard;
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
