package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.ArrayList;

// This class models a standard Resource card
public class InitialCard extends PlayableCard {
    // Constructor for a resource card, in fact this is the same as the playable cards one.
    public InitialCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                       ArrayList<Resource> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite, corners, centerBackResources);
    }
}
