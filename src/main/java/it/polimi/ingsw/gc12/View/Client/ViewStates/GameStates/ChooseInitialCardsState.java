package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

public class ChooseInitialCardsState extends GameScreenState{

    public ChooseInitialCardsState() {
        TUICommands = List.of(
                "'[pickInitial | pi] <side> [front | back]' to place your initial card",
                "'[broadcastMessage | bm] <message>' to send a message to all players (max 200 chars)",
                "'[directMessage | dm] <recipient> <message>' to send a private message (max 200 chars)",
                "Remember that you can always type 'quit' and then reconnect to this game"
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

    @Override
    public String toString() {
        return "Initial Card side choice phase";
    }
}
