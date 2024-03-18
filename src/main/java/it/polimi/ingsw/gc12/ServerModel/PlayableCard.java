package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.Arrays;

// This class models a standard playable card (that is, all cards, except Objective cards)
public class PlayableCard extends Card {
    private Resource[][] corners = new Resource[2][4]; // Front and back, with four corners each
    private ArrayList<Resource> centerBackResources = new ArrayList<Resource>(); // A list of resources present
    // on the card's back: isEmpty() == true if none

    // Constructor that creates a playable card from data read from JSON
    public PlayableCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                        ArrayList<Resource> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite);
        this.corners = new Resource[2][];
        this.corners[0] = Arrays.copyOf(corners[0], corners[0].length);
        this.corners[1] = Arrays.copyOf(corners[1], corners[1].length);
        this.centerBackResources = new ArrayList<Resource>(centerBackResources);
    }

    // Getter method for a single resource on the specified corner
    public Resource getCornerResource(int cornerPosition, Side side) {
        if(side.equals(Side.FRONT)) return corners[0][cornerPosition];
        if(side.equals(Side.BACK)) return corners[1][cornerPosition];
        //TODO: add UndefinedCardSideException?
        return null;
    }

    // Getter method for the array of corners
    public Resource[] getCorners(Side side) {
        if(side.equals(Side.FRONT)) return Arrays.copyOf(corners[0], corners[0].length);
        if(side.equals(Side.BACK)) return Arrays.copyOf(corners[1], corners[1].length);
        //TODO: add UndefinedCardSideException ?
        return null;
    }

    // Getter method for the resources in the center of the back of the card
    public ArrayList<Resource> getCenterBackResources() {
        return new ArrayList<Resource>(centerBackResources);
    }
}

// getCornerresource() -> __ test
//                        Try if we need excpetion
//
// getCenterBackResources() (Getter) -> No test
