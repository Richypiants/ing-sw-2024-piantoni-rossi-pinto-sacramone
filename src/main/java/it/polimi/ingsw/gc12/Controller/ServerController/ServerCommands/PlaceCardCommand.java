package it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class PlaceCardCommand implements ServerCommand {

    private final GenericPair<Integer, Integer> COORDINATES;
    private final int CARD_ID;
    private final Side PLAYED_SIDE;

    public PlaceCardCommand(GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        this.COORDINATES = coordinates;
        this.CARD_ID = cardID;
        this.PLAYED_SIDE = playedSide;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.placeCard(caller, COORDINATES, CARD_ID, PLAYED_SIDE);
    }
}
