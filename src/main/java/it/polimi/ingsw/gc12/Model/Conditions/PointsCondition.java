package it.polimi.ingsw.gc12.Model.Conditions;

import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Model.InGamePlayer;

/**
A standard method to compute conditions to assign points.
These condition will be detailed in subclasses after being read from JSON
 */
public interface PointsCondition {

    /**
    Computes how many times a condition is satisfied
     */
    int numberOfTimesSatisfied(Card thisCard, InGamePlayer target);
}
