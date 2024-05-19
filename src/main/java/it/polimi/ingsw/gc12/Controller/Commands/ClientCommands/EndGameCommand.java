package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public class EndGameCommand implements ClientCommand {

    private final List<Triplet<String, Integer, Integer>> POINTS_STATS;
    private final boolean GAME_ENDED_DUE_TO_DISCONNECTIONS;

    public EndGameCommand(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections) {
        this.POINTS_STATS = pointsStats;
        this.GAME_ENDED_DUE_TO_DISCONNECTIONS = gameEndedDueToDisconnections;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.endGame(POINTS_STATS, GAME_ENDED_DUE_TO_DISCONNECTIONS);
    }
}
