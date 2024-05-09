package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;

public class LeaderboardScreenState extends ViewState {

    private final ArrayList<Triplet<String, Integer, Integer>> POINT_STATS;

    public LeaderboardScreenState(ArrayList<Triplet<String, Integer, Integer>> pointStats) {
        this.POINT_STATS = pointStats;
    }

    @Override
    public void executeState() {
        ClientController.getInstance().view.showLeaderboard(POINT_STATS);
        /*ClientController.getInstance().viewState = new LobbyScreenState();
        ClientController.getInstance().viewState.executeState();*/
    }
}
