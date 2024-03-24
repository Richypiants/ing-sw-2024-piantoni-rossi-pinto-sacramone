package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Game;

public class SetupState extends GameState {

    public SetupState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    //FIXME: might be useless and thus might remove this
    @Override
    public void placeCommonCards() {
    }

    @Override
    public GameState transition() {
        super.transition();

        return new ChooseInitialCardsState(GAME);
    }
}
