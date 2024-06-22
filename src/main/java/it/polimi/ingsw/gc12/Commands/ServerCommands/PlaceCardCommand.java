package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

/**
 * Represents a server command for a client to place a card on the board.
 * Implements the {@link ServerCommand} interface.
 */
public class PlaceCardCommand implements ServerCommand {

    private final GenericPair<Integer, Integer> COORDINATES;
    private final int CARD_ID;
    private final Side PLAYED_SIDE;

    /**
     * Constructs a PlaceCardCommand with the specified coordinates, card ID, and side to be played.
     *
     * @param coordinates The coordinates where the card should be placed on the board.
     * @param cardID      The ID of the card to be placed.
     * @param playedSide  The side (front or back) on which the card should be played.
     */
    public PlaceCardCommand(GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        this.COORDINATES = coordinates;
        this.CARD_ID = cardID;
        this.PLAYED_SIDE = playedSide;
    }

    /**
     * Executes the command by requesting to the server controller to place the card on the board.
     *
     * @param caller           The network session of the client initiating the command.
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.placeCard(caller, COORDINATES, CARD_ID, PLAYED_SIDE);
    }
}

