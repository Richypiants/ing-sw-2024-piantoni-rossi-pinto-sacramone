package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

public class PlayerTurnPlayState extends GameScreenState {

    public PlayerTurnPlayState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showHand();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition, playedSide);
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new PlayerTurnDrawState();
    }
}
