package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.EnumMap;

/*
 A model for a standard Resource card
 */
public final class ResourceCard extends PlayableCard {

    /*
    Generates a resource card from the given parameters (in fact, this is the same as the playable cards' one).
     */
    public ResourceCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                        EnumMap<Resource, Integer> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite, centerBackResources, corners);
    }

    @Override
    public String toString() {
        return "ResourceCard{} " + super.toString();
    }
}
