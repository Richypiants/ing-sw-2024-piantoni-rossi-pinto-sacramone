package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

public class LobbyScreenState extends ViewState {

    public LobbyScreenState() {
        selectedView.lobbyScreen();
    }

    @Override
    public void transition() {
        currentState = new GameScreenState();
    }
}
