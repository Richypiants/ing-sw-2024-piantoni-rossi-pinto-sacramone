package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PickObjectiveCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

import java.util.NoSuchElementException;

public class ChooseObjectiveCardsState extends GameScreenState {

    public ChooseObjectiveCardsState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showObjectiveCardsChoice();
    }

    @Override
    public void pickObjective(int selection){
        //Selection should be [0,1]
        ClientCard card = null;
        final int OFFSET_IN_HAND = 2;
        try {
            card = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand().get(selection + OFFSET_IN_HAND);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Nessuna carta presente alla posizione specificata della mano");
        }

        ClientController.getInstance().requestToServer(new PickObjectiveCommand(card.ID));
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new PlayerTurnPlayState();
    }
}
