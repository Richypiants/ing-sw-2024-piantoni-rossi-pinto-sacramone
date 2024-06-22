package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

/**
 * Represents the leaderboard screen state of the client-side view.
 * Extends {@link ViewState}.
 */
public class LeaderboardScreenState extends ViewState {

    private final List<Triplet<String, Integer, Integer>> POINTS_STATS;
    private final boolean GAME_ENDED_DUE_TO_DISCONNECTIONS;

    /**
     * Constructs a new LeaderboardScreenState with the specified points statistics and game end status.
     *
     * @param pointStats                  The list of triplets containing player nickname, points, and rank.
     * @param gameEndedDueToDisconnections Indicates if the game ended due to player disconnections.
     */
    public LeaderboardScreenState(List<Triplet<String, Integer, Integer>> pointStats, boolean gameEndedDueToDisconnections) {
        this.POINTS_STATS = pointStats;
        this.GAME_ENDED_DUE_TO_DISCONNECTIONS = gameEndedDueToDisconnections;
    }

    /**
     * Executes the behavior of the leaderboard screen state by displaying the leaderboard screen on the selected view.
     */
    @Override
    public void executeState() {
        selectedView.leaderboardScreen(POINTS_STATS, GAME_ENDED_DUE_TO_DISCONNECTIONS);
    }

    /**
     * Transitions to the lobbies screen state.
     * Leaves the current lobby or room in the view model, transitions to the lobbies screen state,
     * and executes the state behavior.
     */
    @Override
    public void toLobbies() {
        CLIENT_CONTROLLER.VIEWMODEL.leaveRoom();
        currentState = new LobbiesScreenState();
        currentState.executeState();
    }

    /**
     * Returns a string representation of the leaderboard screen state.
     *
     * @return The string "leaderboard".
     */
    @Override
    public String toString() {
        return "leaderboard";
    }
}
