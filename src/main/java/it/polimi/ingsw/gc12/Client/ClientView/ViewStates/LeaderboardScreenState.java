package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

public class LeaderboardScreenState extends ViewState {

    @Override
    public void transition() {
        currentState = new LobbyScreenState();
    }
}
