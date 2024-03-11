package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;

// This class models a standard Resource card
public class ResourceCard extends PlayableCard {
    // Constructor for a resource card, in fact this is the same as the one of playable cards
    public ResourceCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                        ArrayList<Resource> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite, corners, centerBackResources);
    }
}
