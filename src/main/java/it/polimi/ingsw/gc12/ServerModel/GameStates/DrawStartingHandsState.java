package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;

public class DrawStartingHandsState extends GameState {

    public DrawStartingHandsState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void drawInitialHand() {
        for (InGamePlayer target : super.GAME.getPlayers()) {
            target.addCardToHand((PlayableCard) GAME.getResourceCardsDeck().draw());
            target.addCardToHand((PlayableCard) GAME.getResourceCardsDeck().draw());
            target.addCardToHand((PlayableCard) GAME.getGoldCardsDeck().draw());
        }
    }

    @Override
    public void transition() {
        super.transition();

        GAME.setState(new ChooseObjectiveCardsState(GAME));
    }
}
