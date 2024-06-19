package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

public class PlayerTurnPlayState extends GameState {

    public PlayerTurnPlayState(GameController controller, Game thisGame) {
        super(controller, thisGame, "playState");
    }

    @Override
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card,
                                       Side playedSide)
            throws UnexpectedPlayerException, CardNotInHandException, NotEnoughResourcesException,
            InvalidCardPositionException {
        if (!target.equals(GAME.getCurrentPlayer()))
            throw new UnexpectedPlayerException();

        GAME.placeCard(target, coordinates, card, playedSide);

        transition();
    }

    @Override
    public void playerDisconnected(InGamePlayer target){
        transition();
    }

    @Override
    public void transition() {
        if (GAME.getFinalPhaseCounter() == -1)
            if (GAME.getCurrentPlayer().getPoints() >= 20)
                GAME.initializeFinalPhaseCounter();
        //TODO: send alert a tutti i giocatori che si è entrati nella fase finale?

        GAME_CONTROLLER.setState(new PlayerTurnDrawState(GAME_CONTROLLER, GAME));

        //Check if there's a card that can be drawn, if not, directly call the transition of the PlayerTurnDrawState
        //The condition is computed in or (||) with the case that the currentPlayer is disconnected, so the PlayerTurnDrawState has to be skipped as well.
        if (checkNoCardsToDraw() || !GAME.getCurrentPlayer().isActive())
            GAME_CONTROLLER.getCurrentState().transition();
    }

    public boolean checkNoCardsToDraw() {
        return GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()
                && GAME.getPlacedResources()[0] == null && GAME.getPlacedResources()[1] == null &&
                GAME.getPlacedGolds()[0] == null && GAME.getPlacedGolds()[1] == null;
    }
}
