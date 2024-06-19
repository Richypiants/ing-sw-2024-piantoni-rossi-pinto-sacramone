package it.polimi.ingsw.gc12.Model.Server.Cards;

import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.Map;

/**
 * Represents a Resource Card in the game's card set. Resource Cards are a specific type of PlayableCard
 * that players use to gain resources and in some cases points.
 */
public final class ResourceCard extends PlayableCard {

    /**
     * Constructs a Resource Card with the specified parameters.
     *
     * @param id                   The unique identifier for the card.
     * @param pointsGranted        The base points granted by this card.
     * @param centerBackResources  The resources located at the center back of the card.
     * @param corners              The resources located at the corners of the card, categorized by sides.
     */
    public ResourceCard(int id, int pointsGranted, Map<Resource, Integer> centerBackResources,
                        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners) {
        super(id, pointsGranted, centerBackResources, corners);
    }

    /**
     * Returns a string representation of the Resource Card, including its unique ID, points granted,
     * corners resources, and center back resources.
     *
     * @return A string representation of the Resource Card.
     */
    @Override
    public String toString() {
        return "(ResourceCard) " + super.toString();
    }
}
