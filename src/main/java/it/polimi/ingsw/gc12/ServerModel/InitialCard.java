package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Map;

/*
A template for an Initial card from the game's cards set
 */
public final class InitialCard extends PlayableCard {

    /*
    Generates an Initial card from the given parameters
     */
    public InitialCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners,
                       Map<Resource, Integer> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite, centerBackResources, corners);
    }

    @Override
    public String toString() {
        return "InitialCard{" +
                "ID=" + ID +
                ", POINTS_GRANTED=" + POINTS_GRANTED +
                ", FRONT_SPRITE=" + FRONT_SPRITE +
                ", BACK_SPRITE=" + BACK_SPRITE +
                '}';
    }
}
