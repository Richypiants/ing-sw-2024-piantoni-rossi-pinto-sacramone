package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class PlayerTurnPlayState extends GameScreenState {

    public PlayerTurnPlayState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.gameScreen();
    }

    @Override
    public void placeCard() {

    }
}
