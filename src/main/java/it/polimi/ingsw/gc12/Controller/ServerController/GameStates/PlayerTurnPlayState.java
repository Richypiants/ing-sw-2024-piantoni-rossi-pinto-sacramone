package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class PlayerTurnPlayState extends GameState {

    public PlayerTurnPlayState(GameController controller, Game thisGame) {
        super(controller, thisGame, "playState");
    }

    @Override
    public synchronized void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card,
                                       Side playedSide)
            throws UnexpectedPlayerException, CardNotInHandException, NotEnoughResourcesException,
            InvalidCardPositionException {
        if (!target.equals(GAME.getCurrentPlayer()))
            throw new UnexpectedPlayerException();

        target.placeCard(coordinates, card, playedSide);

        System.out.println("[SERVER]: Sending card placed by current player to clients in "+ GAME.toString());
        for (var player : GAME.getActivePlayers())
            try {
                keyReverseLookup(GameController.activePlayers, player::equals).getListener().notified(
                        new PlaceCardCommand(target.getNickname(), coordinates, card.ID, playedSide,
                                target.getOwnedResources(), target.getOpenCorners(), target.getPoints()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        transition();
        //FIXME: controllare che non si possa giocare due carte nello stesso turno! in teoria rendendo atomica
        // questa intera funzione dovrebbe garantirlo
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

        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in "+ GAME.toString());
        notifyTransition(GAME.getActivePlayers(), GAME.getRoundNumber(), GAME.getPlayers().indexOf(GAME.getCurrentPlayer()));

        GAME_CONTROLLER.setState(new PlayerTurnDrawState(GAME_CONTROLLER, GAME));

        //Check if there's a card that can be drawn, if not, directly call the transition of the PlayerTurnDrawState
        //The condition is computed in or(||) with the case that the currentPlayer is disconnected, so the PlayerTurnDrawState has to be skipped as well.
        if(GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()
                && GAME.getPlacedResources().length == 0 && GAME.getPlacedGolds().length == 0
                || !GAME.getCurrentPlayer().isActive()) {

            GAME_CONTROLLER.getCurrentState().transition();
        }
    }
}
