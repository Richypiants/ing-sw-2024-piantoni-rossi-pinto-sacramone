package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class ConnectToServerScreenState extends ViewState {

    public ConnectToServerScreenState() {
        ClientController.getInstance().view.connectToServerScreen();
    }

    @Override
    public void connectedConfirmation(){
        ClientController.getInstance().view.connectedConfirmation();
        transition();
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new LobbyScreenState();
    }
}
