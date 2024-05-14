package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public class EndGameCommand implements ClientCommand {

    private final List<Triplet<String, Integer, Integer>> POINTS_STATS;

    public EndGameCommand(List<Triplet<String, Integer, Integer>> pointsStats) {
        this.POINTS_STATS = pointsStats;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.endGame(POINTS_STATS);
    }
}
