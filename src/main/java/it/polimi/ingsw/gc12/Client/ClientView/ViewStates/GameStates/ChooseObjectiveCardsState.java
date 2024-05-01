package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.PickObjectiveCommand;
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
    public void placeCard(int inHandPosition) {
        ClientCard card = null;
        try {
            card = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand().get(inHandPosition);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("nessuna carta presente alla posizione specificata della mano");
        }

        try {
            ClientController.getInstance().requestToServer(new PickObjectiveCommand(card.ID));
        } catch (Exception e) {
            ClientController.getInstance().view.printError(e);
        }
    }
}
