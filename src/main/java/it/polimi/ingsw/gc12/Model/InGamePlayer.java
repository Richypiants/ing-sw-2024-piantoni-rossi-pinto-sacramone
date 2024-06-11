package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ConfirmSelectionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReceiveCardCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReceiveObjectiveChoice;
import it.polimi.ingsw.gc12.Listeners.Listenable;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Represents a player who is currently in a game.
 *
 * This class extends the basic {@link Player} class to include attributes and methods
 * specific to a player who is in a game. It maintains the player's hand of cards,
 * their owned resources, their field, their secret objective, and their points.
 * Additionally, it keeps track of the player's activity or inactivity due to
 * network or voluntary disconnections.
 *
 * The class also implements the {@link Listenable} interface to allow for
 * registering, removing, and notifying listeners about various in-game events
 * such as card placements, addition of card in hand and secret objective related updates.
 */
public class InGamePlayer extends Player implements Listenable {

    /**
     * The list containing all the listeners subscribed to this instance of InGamePlayer.
     */
    private final CopyOnWriteArrayList<Listener> PLAYER_LISTENERS;

    /**
     * The cards currently held in this player's hand.
     */
    private final ArrayList<PlayableCard> CARDS_IN_HAND;

    /**
     * The resources currently owned by this player.
     */
    private final EnumMap<Resource, Integer> OWNED_RESOURCES;

    /**
     * The field of cards placed by this player.
     */
    private final Field OWN_FIELD;

    /**
     * The active status of this player, indicating whether the player is currently connected to the game and playing.
     */
    private boolean active;
    /**
     * The points currently accumulated by this player.
     */
    private int points;

    /**
     * The secret objective card chosen by this player.
     */
    private ObjectiveCard secretObjective;

    /**
     * Constructs an InGamePlayer from the given Player instance.
     * Initializes the player's hand, resources, field, active status, and secret objective.
     *
     * @param player The Player instance from which to create the InGamePlayer.
     */
    protected InGamePlayer(Player player) {
        super(player);
        PLAYER_LISTENERS = new CopyOnWriteArrayList<>();
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
        OWN_FIELD = new Field();
        active = true;
        secretObjective = null;

    }

    /**
     * Converts this InGamePlayer back to a Player instance.
     *
     * @return A new Player instance with the same nickname as this InGamePlayer.
     */
    public Player toPlayer(){
        return new Player(getNickname());
    }

    /**
     * Increases this player's points by the specified amount.
     *
     * @param pointsToAdd The amount of points to add.
     */
    public void increasePoints(int pointsToAdd) {
        points += pointsToAdd;
    }

    /**
     * Returns this player's current points.
     *
     * @return The current points of this player.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns the activity status of this player.
     *
     * @return {@code true} if the player is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Toggles the active status of this player.
     * If the player is currently active, they will become inactive, and vice versa.
     */
    public void toggleActive() {
        active = !active;
    }

    /**
     * Returns the list of cards in this player's hand.
     *
     * @return A copy of the list of PlayableCards in the player's hand.
     */
    public ArrayList<PlayableCard> getCardsInHand() {
        return new ArrayList<>(CARDS_IN_HAND);
    }

    /**
     * Places a card from the player's hand onto their field at the specified coordinates.
     * Updates the player's points and resources based on the card's placement.
     * After being placed, if the given card covers corners of other cards which contains resources,
     * their amount is decremented.
     *
     * @param coordinates The pair of coordinates where the card should be placed.
     * @param card        The card to place.
     * @param playedSide  The side of the card that is played.
     * @throws CardNotInHandException       if the card is not in the player's hand while trying to place it.
     * @throws NotEnoughResourcesException  if the player does not have enough resources to play a GoldCard.
     * @throws InvalidCardPositionException if the card cannot be placed at the specified coordinates.
     */
    protected void placeCard(GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {
        if (!getCardsInHand().contains(card))
            throw new CardNotInHandException();
        if (card instanceof GoldCard && playedSide.equals(Side.FRONT))
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

        if (playedSide.equals(Side.FRONT))
            increasePoints(card.awardPoints(this));

        CARDS_IN_HAND.remove(card);
    }

    /**
     * Adds the specified card to this player's hand.
     *
     * This method adds the given card to the player's hand and
     * notifies all registered listeners about the new card addition with a {@link ReceiveCardCommand}.
     *
     * @param pickedCard The card to add to the player's hand.
     */
    public void addCardToHand(PlayableCard pickedCard) {
        CARDS_IN_HAND.add(pickedCard);
        notifyListeners(new ReceiveCardCommand(List.of(pickedCard.ID)));
    }

    /**
     * Increments the specified resource by the given amount.
     *
     * @param resource            The resource to increment.
     * @param numberToBeIncreased The amount to increment the resource by.
     */
    protected void incrementOwnedResource(Resource resource, int numberToBeIncreased){
        OWNED_RESOURCES.put(resource, OWNED_RESOURCES.get(resource) + numberToBeIncreased);
    }

    /**
     * Returns a copy of the map of resources owned by this player.
     *
     * @return A copy of the EnumMap containing the player's resources.
     */
    public EnumMap<Resource, Integer> getOwnedResources() {
        return new EnumMap<>(OWNED_RESOURCES);
    }

    /**
     * Returns the cards placed by this player on their field.
     *
     * @return A LinkedHashMap of the coordinates to pairs of placed cards and their sides.
     */
    public LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> getPlacedCards() {
        return OWN_FIELD.getPlacedCards();
    }

    /**
     * Returns the available positions where this player can place the next cards.
     *
     * @return An ArrayList of coordinates where cards can be placed.
     */
    public ArrayList<GenericPair<Integer, Integer>> getOpenCorners() {
        return OWN_FIELD.getOpenCorners();
    }

    /**
     * Sets the selection of objective cards for this player.
     *
     * This method assigns the given list of objective cards as the player's selection and
     * notifies all registered listeners about the new objective choice with a {@link ReceiveObjectiveChoice}.
     *
     * @param personalObjectiveCardsSelection The list of Objective Cards selected for this player.
     */
    public void setObjectivesSelection(ArrayList<ObjectiveCard> personalObjectiveCardsSelection) {
        notifyListeners(new ReceiveObjectiveChoice(personalObjectiveCardsSelection.stream()
                .map((card) -> card.ID)
                .toList())
        );
    }

    /**
     * Returns this player's secret Objective Card.
     *
     * @return The secret Objective Card of this player.
     */
    public ObjectiveCard getSecretObjective() {
        return secretObjective;
    }

    /**
     * Sets this player's secret Objective Card.
     *
     * This method assigns the given Objective Card as the player's secret objective and
     * notifies all registered listeners about the selection confirmation with a {@link ConfirmSelectionCommand}.
     *
     * @param objectiveCard The Objective Card to set as this player's secret objective.
     */
    public void setSecretObjective(ObjectiveCard objectiveCard) {
        this.secretObjective = objectiveCard;
        notifyListeners(new ConfirmSelectionCommand(objectiveCard.ID));
    }

    /**
     * Returns the coordinates of the specified card on this player's field.
     *
     * @param placedCard The card whose coordinates are to be retrieved.
     * @return The coordinates of the specified card.
     */
    public GenericPair<Integer, Integer> getCardCoordinates(PlayableCard placedCard) {
        return OWN_FIELD.getCardCoordinates(placedCard);
    }

    /**
     * Adds a listener to the list of this player's listeners.
     *
     * This method ensures thread-safe addition of listeners to the list.
     *
     * @param listener The listener to be added.
     */
    @Override
    public void addListener(Listener listener) {
        synchronized (PLAYER_LISTENERS) {
            PLAYER_LISTENERS.add(listener);
        }
    }

    /**
     * Removes a listener from the list of this player's listeners.
     *
     * This method ensures thread-safe removal of listeners from the list.
     *
     * @param listener The listener to be removed.
     */
    @Override
    public void removeListener(Listener listener) {
        synchronized (PLAYER_LISTENERS) {
            PLAYER_LISTENERS.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners with the specified command.
     *
     * This method ensures thread-safe iteration over the listeners list while notifying them.
     *
     * @param command The command to be sent to all listeners.
     */
    @Override
    public void notifyListeners(ClientCommand command) {
        synchronized (PLAYER_LISTENERS) {
            for (var listener : PLAYER_LISTENERS)
                listener.notified(command);
        }
    }
}