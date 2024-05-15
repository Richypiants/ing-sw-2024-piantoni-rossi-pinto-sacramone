package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public class LeaderboardScreenState extends ViewState {

    private final List<Triplet<String, Integer, Integer>> POINTS_STATS;

    public LeaderboardScreenState(List<Triplet<String, Integer, Integer>> pointStats) {
        this.POINTS_STATS = pointStats;
    }

    @Override
    public void executeState() {
        ClientController.getInstance().view.showLeaderboard(POINTS_STATS);
    }

    @Override
    public void quit() {
        ClientController.getInstance().viewState = new LobbyScreenState();
        ClientController.getInstance().viewState.executeState();
    }
}
