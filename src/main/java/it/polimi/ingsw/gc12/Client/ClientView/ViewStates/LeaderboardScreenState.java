package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class LeaderboardScreenState extends ViewState {

    public LeaderboardScreenState() {
    }

    @Override
    public void executeState() {
        ClientController.getInstance().viewState = new LobbyScreenState();
        ClientController.getInstance().viewState.executeState();
    }
}
