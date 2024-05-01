package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.EnumMap;

public class PlaceCardCommand implements ClientCommand {

    private final String NICKNAME;
    private final GenericPair<Integer, Integer> COORDINATES;
    private final int CARD_ID;
    private final Side PLAYED_SIDE;
    private final EnumMap<Resource, Integer> OWNED_RESOURCE;
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;
    private final int POINTS;

    public PlaceCardCommand(String nickname, GenericPair<Integer, Integer> coordinates, int cardID,
                            Side playedSide, EnumMap<Resource, Integer> ownedResources,
                            ArrayList<GenericPair<Integer, Integer>> openCorners, int points) {
        this.NICKNAME = nickname;
        this.COORDINATES = coordinates;
        this.CARD_ID = cardID;
        this.PLAYED_SIDE = playedSide;
        this.OWNED_RESOURCE = ownedResources;
        this.OPEN_CORNERS = openCorners;
        this.POINTS = points;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.placeCard(NICKNAME, COORDINATES, CARD_ID, PLAYED_SIDE, OWNED_RESOURCE,
                OPEN_CORNERS, POINTS);
    }
}
