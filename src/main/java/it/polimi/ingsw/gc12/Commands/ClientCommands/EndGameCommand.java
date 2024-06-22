package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

/**
 * Represents a client command to notify the end of the game and send the final points statistics.
 * Implements the {@link ClientCommand} interface.
 */
public class EndGameCommand implements ClientCommand {

    private final List<Triplet<String, Integer, Integer>> POINTS_STATS;
    private final boolean GAME_ENDED_DUE_TO_DISCONNECTIONS;

    /**
     * Constructs an EndGameCommand with the specified points statistics and game end status.
     *
     * @param pointsStats                  The list of triplets containing player nicknames, total points, and objective points.
     * @param gameEndedDueToDisconnections Indicates if the game ended due to disconnections.
     */
    public EndGameCommand(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections) {
        this.POINTS_STATS = pointsStats;
        this.GAME_ENDED_DUE_TO_DISCONNECTIONS = gameEndedDueToDisconnections;
    }

    /**
     * Executes the command on the provided client controller, signaling the end of the game
     * and passing the final point statistics.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.endGame(POINTS_STATS, GAME_ENDED_DUE_TO_DISCONNECTIONS);
    }
}
