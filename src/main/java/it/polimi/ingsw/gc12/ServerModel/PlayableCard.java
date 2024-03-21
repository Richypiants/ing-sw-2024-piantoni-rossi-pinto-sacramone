package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.EnumMap;
import java.util.HashMap;

/*
A standard playable card (that is, all cards except Objective cards)
 */
public abstract class PlayableCard extends Card {

    /*
    The corners of both this card's front and back, four corners each:
    0 = NW, 1 = NE, 2 = SE, 3 = SW
     */
    private final HashMap<Side, HashMap<GenericPair<Integer, Integer>, Resource>> CORNERS;

    /*
    The list of resources in this card's center back
     */
    private final EnumMap<Resource, Integer> CENTER_BACK_RESOURCES;

    /*
    Creates a playable card from the passed parameters
     */
    public PlayableCard(int id, int pointsGranted, Image frontSprite, Image backSprite,
                        EnumMap<Resource, Integer> centerBackResources,
                        HashMap<Side, HashMap<GenericPair<Integer, Integer>, Resource>> corners) {
        super(id, pointsGranted, frontSprite, backSprite);
        this.CORNERS = new HashMap<>(corners);
        this.CENTER_BACK_RESOURCES = new EnumMap<>(centerBackResources);
    }

    /*
    Returns the resource on the specified corner
     */
    public Resource getCornerResource(Side side, int x, int y) {
        return CORNERS.get(side).get(new GenericPair<>(x, y));
        //TODO: add UndefinedCardSideException?
        // return null;
    }

    /*
    Returns the array of resources of this card on the specified side
     */
    public HashMap<GenericPair<Integer, Integer>, Resource> getCorners(Side side) {
        return CORNERS.get(side);
        //TODO: add UndefinedCardSideException ?
        // return null;
    }

    /*
    Returns a copy of the list of resources on this card's center back
     */
    public EnumMap<Resource, Integer> getCenterBackResources() {
        return new EnumMap<>(CENTER_BACK_RESOURCES);
    }

    @Override
    public String toString() {
        return "PlayableCard{" +
                "CORNERS=" + CORNERS +
                ", CENTER_BACK_RESOURCES=" + CENTER_BACK_RESOURCES +
                "} " + super.toString();
    }
}

// getCornerResource() -> __ test
//                        Try if we need excpetion
//                        side undefined
//
// getCorners() -> Si test
//                 - Casi limite
//                   side undefined
//
// getCenterBackResources() (Getter) -> No test
