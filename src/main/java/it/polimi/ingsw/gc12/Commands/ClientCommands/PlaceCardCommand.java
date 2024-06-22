package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Represents a client command to place a card on the game board.
 * Implements the {@link ClientCommand} interface.
 */
public class PlaceCardCommand implements ClientCommand {

    private final String NICKNAME;
    private final GenericPair<Integer, Integer> COORDINATES;
    private final int CARD_ID;
    private final Side PLAYED_SIDE;
    private final EnumMap<Resource, Integer> OWNED_RESOURCE;
    private final ArrayList<GenericPair<Integer, Integer>> OPEN_CORNERS;
    private final int POINTS;


    /**
     * Constructs a PlaceCardCommand with the specified parameters.
     *
     * @param nickname       The nickname of the player placing the card.
     * @param coordinates    The coordinates on the game board where the card is placed.
     * @param cardID         The ID of the card being placed.
     * @param playedSide     The side on which the card is played.
     * @param ownedResources The resources owned by the player after placing the card.
     * @param openCorners    The open corners created on the game board after placing the card.
     * @param points         The points gained by the player after placing the card.
     */
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

    /**
     * Executes the command on the provided client controller, requesting to place the card.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.placeCard(NICKNAME, COORDINATES, CARD_ID, PLAYED_SIDE, OWNED_RESOURCE,
                OPEN_CORNERS, POINTS);
    }
}
