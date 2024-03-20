package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Arrays;
import java.util.EnumMap;

/*
A standard playable card (that is, all cards except Objective cards)
 */
public abstract class PlayableCard extends Card {

    /*
    The corners of both this card's front and back, four corners each:
    0 = NW, 1 = NE, 2 = SE, 3 = SW
     */
    private final Resource[][] CORNERS;

    /*
    The list of resources in this card's center back
     */
    private final EnumMap<Resource, Integer> CENTER_BACK_RESOURCES;

    /*
    Creates a playable card from the passed parameters
     */
    public PlayableCard(int id, int pointsGranted, Image frontSprite, Image backSprite, EnumMap<Resource, Integer> centerBackResources,Resource[][] corners) {
        super(id, pointsGranted, frontSprite, backSprite);
        this.CORNERS = new Resource[2][];
        this.CORNERS[0] = Arrays.copyOf(corners[0], corners[0].length);
        this.CORNERS[1] = Arrays.copyOf(corners[1], corners[1].length);
        this.CENTER_BACK_RESOURCES = new EnumMap<>(centerBackResources);
    }

    /*
    Returns the resource on the specified corner
     */
    public Resource getCornerResource(int cornerPosition, Side side) {
        if (side.equals(Side.FRONT)) return CORNERS[0][cornerPosition];
        if (side.equals(Side.BACK)) return CORNERS[1][cornerPosition];
        // TODO: add UndefinedCardSideException?
        return null;
    }

    /*
    Returns the array of resources of this card on the specified side
     */
    public Resource[] getCorners(Side side) {
        if (side.equals(Side.FRONT)) return Arrays.copyOf(CORNERS[0], CORNERS[0].length);
        if (side.equals(Side.BACK)) return Arrays.copyOf(CORNERS[1], CORNERS[1].length);
        //TODO: add UndefinedCardSideException ?
        return null;
    }

    /*
    returns a copy of the list of resources on this card's center back
     */
    public EnumMap<Resource, Integer> getCenterBackResources() {
        return new EnumMap<>(CENTER_BACK_RESOURCES);
    }

    @Override
    public String toString() {
        return "PlayableCard{" +
                "CORNERS=" + Arrays.toString(CORNERS) +
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
