package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

public class ChooseInitialCardsState extends GameScreenState{

    public ChooseInitialCardsState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showInitialCardsChoice();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition, playedSide);
    }
}
