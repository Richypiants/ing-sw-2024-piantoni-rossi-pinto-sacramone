package it.polimi.ingsw.gc12.Model.Server.Cards;

import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Represents a standard playable card in the game's card set. Playable cards can be played by players
 * and have associated resources and point values. This class is extended by specific types of playable cards.
 */
public abstract class PlayableCard extends Card {

    /**
     * The resources located at the corners of this card's front and back, grouped by their sides.
     * The inner key identifies the resource located at the offset expressed in cartesian coordinates.
     */
    private final Map<Side, Map<GenericPair<Integer, Integer>, Resource>> CORNERS;

    /**
     * The resources located at the center back of this card.
     */
    private final Map<Resource, Integer> CENTER_BACK_RESOURCES;


    /**
     * Constructs a playable card with the specified parameters.
     *
     * @param id                   The unique identifier for the card.
     * @param pointsGranted        The base points granted by this card.
     * @param centerBackResources  The resources located at the center back of the card.
     * @param corners              The resources located at the corners of the card, categorized by sides.
     */
    public PlayableCard(int id, int pointsGranted,
                        Map<Resource, Integer> centerBackResources,
                        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners) {
        super(id, pointsGranted);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> cornersCopy = new HashMap<>(corners);
        cornersCopy.replaceAll((k, v) -> unmodifiableMap(v));
        this.CORNERS = Map.copyOf(cornersCopy);
        this.CENTER_BACK_RESOURCES = Map.copyOf(centerBackResources);
    }

    /**
     * Returns the resource located at the specified corner of the card.
     *
     * @param side The side of the card (front or back).
     * @param x    The x-coordinate of the corner.
     * @param y    The y-coordinate of the corner.
     * @return The resource located at the specified corner.
     */
    public Resource getCornerResource(Side side, int x, int y) {
        return CORNERS.get(side).get(new GenericPair<>(x, y));
    }

    /**
     * Returns the map of resources located at the specified side of the card.
     *
     * @param side The side of the card (front or back).
     * @return The map of resources located on the corners of the specified side.
     */
    public Map<GenericPair<Integer, Integer>, Resource> getCorners(Side side) {
        return CORNERS.get(side);
    }

    /**
     * Returns the map of resources located on center back of the card.
     *
     * @return The map of resources located on the center back.
     */
    public Map<Resource, Integer> getCenterBackResources() {
        return CENTER_BACK_RESOURCES;
    }

    /**
     * Calculates and returns the number of points awarded to the specified player when this card is played.
     * If the card is placed on the back side, no points are awarded.
     *
     * @param target The player who played the card.
     * @return The number of points awarded by playing this card.
     */
    @Override
    public int awardPoints(InGamePlayer target) {
        if (target.getPlacedCards().get(target.getCardCoordinates(this)).getY().equals(Side.BACK))
            return 0;
        else
            return this.POINTS_GRANTED;
    }

    /**
     * Returns a string representation of the playable card, including its unique ID, points granted,
     * corners resources, and center back resources.
     *
     * @return A string representation of the playable card.
     */
    @Override
    public String toString() {
        return super.toString() +
                " {" +
                "CORNERS=" + CORNERS +
                ", CENTER_BACK_RESOURCES=" + CENTER_BACK_RESOURCES +
                "} ";
    }
}

