package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.GoldCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A structure for a player which is currently playing a game
 */
public class InGamePlayer extends Player {

    /**
     * This player's color
     */
    public final Color COLOR = null; //TODO: implement color selection logic
    /**
     * The cards in this player's hand
     */
    private final ArrayList<PlayableCard> CARDS_IN_HAND;
    /**
     * The resources owned by this player currently
     */
    private final EnumMap<Resource, Integer> OWNED_RESOURCES;
    /**
     * The field of this player
     */
    private final Field OWN_FIELD;
    /**
     *
     */
    private boolean isActive = true; //TODO: implement activity management
    /**
     * The points currently gained by this player
     */
    private int points;
    /**
     * The secret Objective Card chosen by this player
     */
    private ObjectiveCard secretObjective;

    /**
     * Constructs an InGamePlayer from the player passed as parameter
     */
    protected InGamePlayer(Player player) {
        super(player);
        CARDS_IN_HAND = new ArrayList<>();
        OWNED_RESOURCES = new EnumMap<>(Resource.class);
        for (Resource r : Arrays.stream(Resource.values())
                .filter((resource) ->
                        !(resource.equals(Resource.EMPTY) || resource.equals(Resource.NOT_A_CORNER))
                )
                .collect(Collectors.toCollection(ArrayList::new))
        ) {
            OWNED_RESOURCES.put(r, 0);
        }
        secretObjective = null;
        OWN_FIELD = new Field();
    }

    public Player toPlayer(){
        return new Player(getNickname());
    }

    /**
     * Given the desired amount to be increased by, updates the player's points
     */
    public void increasePoints(int pointsToAdd) {
        points += pointsToAdd;
    }

    /**
     * Returns this player's current points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns this player's current points
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns this player's current points
     */
    public void toggleActive() {
        isActive = !isActive;
    }

    /**
     * Returns a copy of the list of cards in this player's hand
     */
    public ArrayList<PlayableCard> getCardsInHand() {
        return new ArrayList<>(CARDS_IN_HAND);
    }

    /**
     * Given the card and the desired position, wrapped in a GenericPair structure meaning <x,y> coordinates on
     * the field, places the card into the ownField HashMap, also incrementing the ownedResources by the correct number
     * @requires the given card to place must be in this player's hand (it is contained in CARDS_IN_HAND)
     * @requires if the given card is a GoldCard: this player must have the needed resources to play it
     * @ensures for each Resource contained in a corner or on the back of the given card's played side, this player's
     * resources are incremented by 1
     * @ensures if the given card (after being placed) covers corners of other cards, for each covered corner the
     * amount is decremented by 1
     */
    public void placeCard(GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {
        if (!getCardsInHand().contains(card))
            throw new CardNotInHandException();
        if (card instanceof GoldCard)
            if (((GoldCard) card).getNeededResourcesToPlay().numberOfTimesSatisfied(card, this) <= 0)
                throw new NotEnoughResourcesException();

        OWN_FIELD.addCard(coordinates, card, playedSide);

        //Foreach Corner of the given card that is valid and non-empty, increment the corresponding Resource by 1
        for (var res : card.getCorners(playedSide)
                .values().stream()
                .filter((resource) ->
                        !(resource.equals(Resource.EMPTY) || resource.equals(Resource.NOT_A_CORNER))
                )
                .collect(Collectors.toCollection(ArrayList::new))
        ) {
            incrementOwnedResource(res, 1);
        }

        //If card is played on the back, also increment the amount by 1 for the resources in the center
        if (playedSide.equals(Side.BACK)) {
            card.getCenterBackResources()
                    .forEach(this::incrementOwnedResource);
        }

        //For every corner on the played side of the given card:
        card.getCorners(playedSide).keySet().stream()
                .map((offset) ->
                        //optionally get a given card which gets covered
                        Optional.ofNullable(
                                OWN_FIELD.getPlacedCards().get(
                                        new GenericPair<>(
                                                coordinates.getX() + offset.getX(),
                                                coordinates.getY() + offset.getY()
                                        )
                                )
                                //and optionally get the corresponding resource covered
                        ).flatMap((coveredCorner) -> Optional.of(
                                        coveredCorner.getX()
                                                .getCornerResource(coveredCorner.getY(), -offset.getX(), -offset.getY())
                                )
                        )
                )
                //keep only the non-empty optionals containing found resources
                .filter(Optional::isPresent)
                .map(Optional::get)
                //filter out eventual empties and not_a_corners (can't happen but still) covered and decrement
                .filter((coveredResource) ->
                        !(coveredResource.equals(Resource.NOT_A_CORNER) || coveredResource.equals(Resource.EMPTY))
                ).forEach((coveredResource) -> incrementOwnedResource(coveredResource, -1));

        CARDS_IN_HAND.remove(card);
    }

    /**
     * Adds the pickedCard to the current player's hand
     */
    public void addCardToHand(PlayableCard pickedCard) {
        CARDS_IN_HAND.add(pickedCard);
    }

    /**
     * Given a specific resource type and the quantity to be increased by, updates the HashMap
     */
    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
        OWNED_RESOURCES.put(resource, OWNED_RESOURCES.get(resource) + numberToBeIncreased);
    }

    /**
     * Returns a copy of the map of resources owned by this player
     */
    public EnumMap<Resource, Integer> getOwnedResources() {
        return new EnumMap<>(OWNED_RESOURCES);
    }

    /**
     * Returns the cards placed by this player
     */
    public HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> getPlacedCards() {
        return OWN_FIELD.getPlacedCards();
    }

    /**
     * Returns the available positions where this player can place cards next
     */
    protected ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return OWN_FIELD.getOpenCorners();
    }

    /**
     * Returns this player's secret Objective card
     */
    public ObjectiveCard getSecretObjective() {
        return secretObjective;
    }

    /**
     * Sets this player's secret Objective card
     */
    public void setSecretObjective(ObjectiveCard objectiveCard) {
        //FIXME: if Cards classes' attributes are final, this is fine.
        this.secretObjective = objectiveCard;
    }

    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        return OWN_FIELD.getCardCoordinates(placedCard);
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
