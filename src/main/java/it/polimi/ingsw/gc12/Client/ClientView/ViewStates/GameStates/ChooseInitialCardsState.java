package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.NoSuchElementException;

public class ChooseInitialCardsState extends GameScreenState{

    public ChooseInitialCardsState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showInitialCardsChoice();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        ClientCard card = null;
        try{
            card = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand().get(inHandPosition);
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("nessuna carta presente alla posizione specificata della mano");
        }

        try {
            ClientController.getInstance().requestToServer(new PlaceCardCommand(coordinates, card.ID, playedSide));
        } catch (Exception e) {
            ClientController.getInstance().view.printError(e);
        }
    }
}
