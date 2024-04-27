package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class TitleScreenState extends ViewState {

    public TitleScreenState() {
    }

    @Override
    public void executeState() {
        ClientController.getInstance().view.titleScreen();
        ClientController.getInstance().viewState = new ConnectToServerScreenState();
        ClientController.getInstance().viewState.executeState();
    }

    //TODO: quit anche qui?
}
