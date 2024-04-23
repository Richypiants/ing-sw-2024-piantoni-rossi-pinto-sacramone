package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class LeaderboardScreenState extends ViewState {

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new LobbyScreenState();
    }
}
