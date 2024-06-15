package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.Map;

/**
 * Represents an Initial Card in the game's card set. Initial Cards are a specific type of PlayableCard
 * that players start with or are initially dealt.
 */
public final class InitialCard extends PlayableCard {

    /**
     * Constructs an Initial Card with the specified parameters.
     *
     * @param id                   The unique identifier for the card.
     * @param pointsGranted        The base points granted by this card.
     * @param corners              The resources associated with each corner of the card.
     * @param centerBackResources  The resources associated with the center back of the card.
     */
    public InitialCard(int id, int pointsGranted, Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners,
                       Map<Resource, Integer> centerBackResources) {
        super(id, pointsGranted, centerBackResources, corners);
    }


    /**
     * Returns a string representation of the Initial Card, including its unique ID and points granted.
     *
     * @return A string representation of the Initial Card.
     */
    @Override
    public String toString() {
        return "(InitialCard) " + super.toString();
    }
}
