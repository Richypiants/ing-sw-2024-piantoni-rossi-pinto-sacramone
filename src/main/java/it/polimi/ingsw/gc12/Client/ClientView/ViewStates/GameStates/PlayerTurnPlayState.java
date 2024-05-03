package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.List;

public class PlayerTurnPlayState extends GameScreenState {

    public PlayerTurnPlayState() {
        TUICommands = List.of(
                "'placeCard <x> <y> <inHandPosition> <side>' (x,y): coordinate di piazzamento,",
                "    inHandPosition: [1]...[n], side: [front][back]",
                "'broadcastMessage <message>' per inviare un messaggio in gioco",
                "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco"
        );
    }

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
