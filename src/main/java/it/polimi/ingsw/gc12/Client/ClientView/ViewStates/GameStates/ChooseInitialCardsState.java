package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.List;

public class ChooseInitialCardsState extends GameScreenState{

    public ChooseInitialCardsState() {
        TUICommands = List.of(
                "'pickInitial <side> [front][back],",
                "'broadcastMessage <message>' per inviare un messaggio in gioco (max 200 chars)",
                "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco (max 200 chars)"
                //FIXME: troppo lungo?
        );
    }

    @Override
    public void executeState() {
        super.executeState();

        if(ClientController.getInstance().viewModel.getGame().getThisPlayer().getPlacedCards().containsKey(new GenericPair<>(0,0)))
            ClientController.getInstance().view.gameScreen();
        else
            ClientController.getInstance().view.showInitialCardsChoice();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition - 1, playedSide);
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new ChooseObjectiveCardsState();
    }
}
