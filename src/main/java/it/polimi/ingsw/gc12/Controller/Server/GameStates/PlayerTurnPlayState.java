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

/**
 * Represents the game state where players can place cards on the field if it's their turn.
 * This state handles the placement of cards on the game board.
 */
public class PlayerTurnPlayState extends GameState {

    /**
     * Constructs a PlayerTurnPlayState object with the specified controller and game.
     *
     * @param controller The GameController managing the game flow.
     * @param thisGame   The current Game instance.
     */
    public PlayerTurnPlayState(GameController controller, Game thisGame) {
        super(controller, thisGame, "playState");
    }

    /**
     * Handles the action of a player placing a card on the game board during their turn.
     * Verifies the target player, card position, and side, then places the card accordingly.
     *
     * @param target      The player performing the place action.
     * @param coordinates The coordinates on the game board where the card is to be placed.
     * @param card        The card to be placed.
     * @param playedSide  The side of the card to be played (FRONT or BACK).
     * @throws UnexpectedPlayerException If the player attempting to place a card is not the current player.
     * @throws CardNotInHandException    If the specified card is not in the player's hand.
     * @throws NotEnoughResourcesException If the player does not have enough resources to place the card.
     * @throws InvalidCardPositionException If the specified card position on the board is invalid.
     */
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

    /**
     * Handles the action when a player disconnects during their turn.
     * Immediately transitions to the next game state, skipping their action.
     *
     * @param target The player who disconnected.
     */
    @Override
    public void playerDisconnected(InGamePlayer target){
        transition();
    }

    /**
     * Handles the transition to the next state after the current player's turn.
     * Checks the game state and conditions to determine the next state after the play phase.
     * If no cards can be drawn or the current player is disconnected, transitions to the next play phase.
     * If the final counter has reached the end, transitions to the victory calculation state.
     */
    @Override
    public void transition() {
        // Initialize final phase counter if player reaches 20 points in non-final phase
        if (GAME.getFinalPhaseCounter() == -1)
            if (GAME.getCurrentPlayer().getPoints() >= 20)
                GAME.initializeFinalPhaseCounter();

        GAME_CONTROLLER.setState(new PlayerTurnDrawState(GAME_CONTROLLER, GAME));

        //Check if there's a card that can be drawn, if not, directly call the transition of the PlayerTurnDrawState
        //The condition is computed in or (||) with the case that the currentPlayer is disconnected, so the PlayerTurnDrawState has to be skipped as well.
        if (checkNoCardsToDraw() || !GAME.getCurrentPlayer().isActive())
            GAME_CONTROLLER.getCurrentState().transition();
    }

    /**
     * Checks if there are no cards left to draw from any deck or placed resources.
     *
     * @return true if there are no cards left to draw, false otherwise.
     */
    public boolean checkNoCardsToDraw() {
        return GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()
                && GAME.getPlacedResources()[0] == null && GAME.getPlacedResources()[1] == null &&
                GAME.getPlacedGolds()[0] == null && GAME.getPlacedGolds()[1] == null;
    }
}
