package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

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
        selectedView.gameScreen();
        if (!ClientController.getInstance().VIEWMODEL.getCurrentGame().getThisPlayer().getPlacedCards().containsKey(new GenericPair<>(0, 0)))
            selectedView.showInitialCardsChoice();
    }

    @Override
    public void restoreScreenState(){
        selectedView.gameScreen();
        if (!CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getThisPlayer().getPlacedCards().containsKey(new GenericPair<>(0, 0)))
            selectedView.showInitialCardsChoice();
    }

    @Override
    public void showPlacedCard(String nickname) {
        if (nickname.equals(CLIENT_CONTROLLER.VIEWMODEL.getOwnNickname()))
            selectedView.gameScreen();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition - 1, playedSide);
    }

    @Override
    public void transition() {
    }
}
