package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Map;

/**
A template for an Initial card from the game's cards set
 */
public final class InitialCard extends PlayableCard {

    /**
    Generates an Initial card from the given parameters
     */
    public InitialCard(int id, int pointsGranted, Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners,
                       Map<Resource, Integer> centerBackResources) {
        super(id, pointsGranted, centerBackResources, corners);
    }

    @Override
    public String toString() {
        return "(InitialCard) " + super.toString();
    }
}
