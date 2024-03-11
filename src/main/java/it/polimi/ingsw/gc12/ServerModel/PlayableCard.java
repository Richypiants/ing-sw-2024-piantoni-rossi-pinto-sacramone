package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;

// This class models a standard playable card (that is, all cards, except Objective cards)
public class PlayableCard extends Card {
    private Resource[][] corners = new Resource[2][4]; // Front and back, with four corners each
    private ArrayList<Resource> centerBackResources = new ArrayList<Resource>(); // A list of resources present
    // on the card's back: isEmpty() == true if none

    // Default constructor to create an "empty card slot" to define only legal position where a card can be
    // played each turn
    //FIXME: is this still needed?
    protected PlayableCard() {
        super(0, 0, null, null);
        this.corners = null;
        this.centerBackResources = null;
    }

    // Constructor that creates a playable card from data read from JSON
    public PlayableCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                        ArrayList<Resource> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite);
        //TODO: uncomment and complete the following lines, ensuring parameters are COPIED (!) safely
        //this.corners = ...;
        //this.centerBackResources = ...;
    }

    // Getter method for a single resource on the specified corner
    //FIXME: maybe we should pass the wanted side too, so that we can inspect the card even when not placed?
    public Resource getCornerResource(int cornerPosition) {
        //return corners[getShownSide()][cornerPosition];
        return null;
    }

    // Getter method for the array of corners
    public Resource[] getCorners() {
        //TODO: return a copy of the array, and not the array itself
        return null;
    }

    // Getter method for the resources in the center of the back of the card
    public ArrayList<Resource> getCenterBackResources() {
        //TODO: return a copy of the list, and not the list itself
        return null;
    }
}
