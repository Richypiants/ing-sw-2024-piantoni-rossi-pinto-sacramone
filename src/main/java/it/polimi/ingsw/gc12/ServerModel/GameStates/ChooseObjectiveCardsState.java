package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;

public class ChooseObjectiveCardsState extends GameState {

    public ChooseObjectiveCardsState(Game thisGame) {
        super(thisGame, 0, -1);
    }


    //TODO: Check if the given ObjectiveCard was in the possible choice for the targeted InGamePlayer
    @Override
    public void pickObjective(InGamePlayer player, ObjectiveCard objective) throws AlreadySetCardException {
        if (player.getSecretObjective() == null)
            player.setSecretObjective(objective);
        else
            throw new AlreadySetCardException();
    }


    @Override
    public GameState transition() {
        super.transition();

        return new PlayerTurnPlayState(GAME, 0, -1);
    }
}
