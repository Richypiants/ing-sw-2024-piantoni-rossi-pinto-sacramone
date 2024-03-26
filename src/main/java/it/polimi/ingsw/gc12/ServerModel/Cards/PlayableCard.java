package it.polimi.ingsw.gc12.ServerModel.Cards;

import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/*
A standard playable card (that is, all cards except Objective cards)
 */
public abstract class PlayableCard extends Card {

    /*
    The corners of both this card's front and back, four corners each: the key indicates the offset by which
    coordinates must be moved in order to get to the connected card
     */
    private final Map<Side, Map<GenericPair<Integer, Integer>, Resource>> CORNERS;

    /*
    The list of resources in this card's center back
     */
    private final Map<Resource, Integer> CENTER_BACK_RESOURCES;

    /*
    Creates a playable card from the passed parameters
     */
    public PlayableCard(int id, int pointsGranted, Image frontSprite, Image backSprite,
                        Map<Resource, Integer> centerBackResources,
                        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners) {
        /*if(!corners.keySet().equals())
        for(Map<> map : corners){
            if(corners.keySet().size() != 4)
                throw new MalformedCardException();
            //TODO: find a better name for this: maybe use IllegalArgumentException?

            if(map.keySet().stream()
                    .anyMatch((coordinates) -> !(
                            coordinates.equals(new GenericPair<Integer, Integer>(-1, -1)) ||
                                    coordinates.equals(new GenericPair<Integer, Integer>(-1, 1)) ||
                                    coordinates.equals(new GenericPair<Integer, Integer>(1, -1)) ||
                                    coordinates.equals(new GenericPair<Integer, Integer>(1, 1))
                            )
                    )
            )
                throw new MalformedCardException();
        }*/

        super(id, pointsGranted, frontSprite, backSprite);
        //TODO: add check for pair coordinates (only +-1, +-1)
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> cornersCopy = new HashMap<>(corners);
        cornersCopy.replaceAll((k, v) -> unmodifiableMap(v));
        this.CORNERS = Map.copyOf(cornersCopy);
        this.CENTER_BACK_RESOURCES = Map.copyOf(centerBackResources);
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
    public Map<GenericPair<Integer, Integer>, Resource> getCorners(Side side) {
        return CORNERS.get(side);
        //TODO: add UndefinedCardSideException ?
        // return null;
    }

    /*
    Returns a copy of the list of resources on this card's center back
     */
    public Map<Resource, Integer> getCenterBackResources() {
        return CENTER_BACK_RESOURCES;
    }

    //FIXME: abbastanza orribile, sistemare?
    @Override
    public int awardPoints(InGamePlayer target) {
        if (target.getPlacedCards().get(target.getCardCoordinates(this)).getY().equals(Side.BACK))
            return 0;
        else
            return this.POINTS_GRANTED;
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
