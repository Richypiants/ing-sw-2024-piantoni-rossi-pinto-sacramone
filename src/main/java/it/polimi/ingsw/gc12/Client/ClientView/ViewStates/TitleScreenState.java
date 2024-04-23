package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class TitleScreenState extends ViewState {

    public TitleScreenState() {
        ClientController.getInstance().view.titleScreen();
        transition();
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new ConnectToServerScreenState();
    }
}
