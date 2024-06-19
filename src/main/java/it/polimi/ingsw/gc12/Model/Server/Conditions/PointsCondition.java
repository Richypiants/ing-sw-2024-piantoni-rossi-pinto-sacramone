package it.polimi.ingsw.gc12.Model.Server.Conditions;

import it.polimi.ingsw.gc12.Model.Server.Cards.Card;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;

/**
 * A standard method to compute conditions to assign points.
 * These conditions will be detailed in subclasses that depend on the specific card representation.
 */
public interface PointsCondition {

    /**
     * Computes how many times a condition is satisfied.
     *
     * @param thisCard The card being played, which contains a specific subclass of PointsCondition,
     *                 which is evaluated accordingly.
     * @param target   The player who is playing the card.
     * @return The number of times the pattern condition is satisfied.
     */
    int numberOfTimesSatisfied(Card thisCard, InGamePlayer target);
}
