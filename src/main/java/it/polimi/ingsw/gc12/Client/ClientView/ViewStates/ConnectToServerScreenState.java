package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.CreatePlayerCommand;

public class ConnectToServerScreenState extends ViewState {

    public ConnectToServerScreenState() {
        selectedView.connectToServerScreen();
        transition();
    }

    @Override
    public void connectedConfirmation(){
        ClientController.getInstance().view.connectedConfirmation();
        transition();
    }

    @Override
    public void transition() {
        currentState = new LobbyScreenState();
    }
}
