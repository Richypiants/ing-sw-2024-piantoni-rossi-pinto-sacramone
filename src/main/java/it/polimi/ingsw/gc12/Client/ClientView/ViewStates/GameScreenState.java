package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

public class GameScreenState extends ViewState {

    @Override
    public void transition() {
        currentState = new LeaderboardScreenState();
    }
}
