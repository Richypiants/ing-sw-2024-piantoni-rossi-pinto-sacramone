package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a client-side player extending the {@link Player} class.
 * This class manages the player's state, including resources, placed cards,
 * open corners for placing new cards, and points.
 */
public class ClientPlayer extends Player implements Serializable {

    /**
     * The resources currently owned by this player.
     */
    private EnumMap<Resource, Integer> ownedResources;
    /**
     * The cards placed by this player on their field, mapped by coordinates.
     */
    private final LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<ClientCard, Side>> PLACED_CARDS;
    /**
     * The coordinates where this player can currently place new cards.
     */
    private List<GenericPair<Integer, Integer>> openCorners;
    /**
     * The points currently gained by this player.
     */
    private int points;
    /**
     * Indicates whether the player is currently active.
     */
    private boolean isActive = true; //TODO: implement visual activity management

    /**
     * Constructs a new {@code ClientPlayer} with the specified parameters.
     *
     * @param player the {@link Player} object to base this client player on
     * @param openCorners the list of positions where the player can place new cards
     * @param ownedResources the resources currently owned by the player
     * @param playerPoints the points currently gained by the player
     */
    public ClientPlayer(Player player, List<GenericPair<Integer, Integer>> openCorners,
            EnumMap<Resource, Integer> ownedResources, int playerPoints){
        super(player);
        this.openCorners = openCorners;
        this.ownedResources = ownedResources;
        this.PLACED_CARDS = new LinkedHashMap<>();
        this.points = playerPoints;
    }

    /**
     * Returns the resources currently owned by this player.
     *
     * @return the map of resources and their quantities
     */
    public EnumMap<Resource, Integer> getOwnedResources(){
        return ownedResources;
    }

    /**
     * Sets the resources owned by this player.
     *
     * @param ownedResources the map of resources and their quantities to set
     */
    public void setOwnedResources(EnumMap<Resource, Integer> ownedResources){
        this.ownedResources = ownedResources;
    }

    /**
     * Returns the cards placed by this player on their field.
     *
     * @return the map containing the coordinates where a card is placed and on which side
     */
    public LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<ClientCard, Side>> getPlacedCards(){
        return PLACED_CARDS;
    }

    /**
     * Places a card on the field at the specified coordinates.
     *
     * @param coordinates the coordinates to place the card at
     * @param card the card to be placed
     * @param playedSide the side of the card to be played
     */
    public void placeCard(GenericPair<Integer, Integer> coordinates, ClientCard card, Side playedSide){
        PLACED_CARDS.put(coordinates, new GenericPair<>(card, playedSide));
    }

    /**
     * Returns the positions where this player can currently place new cards.
     *
     * @return the list of coordinates representing open corners
     */
    public List<GenericPair<Integer, Integer>> getOpenCorners() {
        return openCorners;
    }

    /**
     * Sets the positions where this player can currently place new cards.
     *
     * @param openCorners the list of coordinates representing open corners to set
     */
    public void setOpenCorners(List<GenericPair<Integer, Integer>> openCorners) {
        this.openCorners = openCorners;
    }

    /**
     * Returns the points currently gained by this player.
     *
     * @return the player's points
     */
    public int getPoints(){
        return points;
    }

    /**
     * Sets the points currently gained by this player.
     *
     * @param points the points to set
     */
    public void setPoints(int points){
        this.points = points;
    }

    /**
     * Returns whether the player is currently active.
     *
     * @return {@code true} if the player is active, {@code false} otherwise
     */
    public boolean isActive(){
        return isActive;
    }

    /**
     * Toggles the player's active status.
     */
    public void toggleActive(){
        isActive = !isActive;
    }
}
