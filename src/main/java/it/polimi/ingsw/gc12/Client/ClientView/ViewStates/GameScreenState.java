package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class GameScreenState extends ViewState {

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new LeaderboardScreenState();
    }
}
