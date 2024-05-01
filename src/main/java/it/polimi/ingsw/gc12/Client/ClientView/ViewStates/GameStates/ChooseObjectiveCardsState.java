package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

public class ChooseObjectiveCardsState extends GameScreenState {

    public ChooseObjectiveCardsState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showObjectiveCardsChoice();
    }

    @Override
    public void placeCard() {

    }
}
