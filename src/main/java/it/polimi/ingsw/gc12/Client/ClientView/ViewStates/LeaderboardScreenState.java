package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public class LeaderboardScreenState extends ViewState {

    private final List<Triplet<String, Integer, Integer>> POINTS_STATS;
    private final boolean GAME_ENDED_DUE_TO_DISCONNECTIONS;

    public LeaderboardScreenState(List<Triplet<String, Integer, Integer>> pointStats, boolean gameEndedDueToDisconnections) {
        this.POINTS_STATS = pointStats;
        this.GAME_ENDED_DUE_TO_DISCONNECTIONS = gameEndedDueToDisconnections;
    }

    @Override
    public void executeState() {
        selectedView.showLeaderboard(POINTS_STATS, GAME_ENDED_DUE_TO_DISCONNECTIONS);
    }

    @Override
    public void toLobbies() {
        CLIENT_CONTROLLER.VIEWMODEL.leaveRoom();
        currentState = new LobbyScreenState();
        currentState.executeState();
    }
}
