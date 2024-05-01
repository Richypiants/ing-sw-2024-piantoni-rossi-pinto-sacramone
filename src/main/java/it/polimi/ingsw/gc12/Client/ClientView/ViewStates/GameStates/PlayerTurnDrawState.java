package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class PlayerTurnDrawState extends GameScreenState {

    public PlayerTurnDrawState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showField();
    }

    @Override
    public void drawFromDeck() {

    }

    @Override
    public void drawFromVisibleCards() {

    }
}
