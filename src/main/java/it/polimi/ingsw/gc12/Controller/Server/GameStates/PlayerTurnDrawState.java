package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidDeckPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnknownStringException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the game state where players draw cards during their turn.
 * This state handles drawing cards from various decks and placed resources.
 */
public class PlayerTurnDrawState extends GameState {


    /**
     * List of lambdas that perform specific drawing actions, achieving a complete routine of operations to ensure .
     * Each lambda corresponds to a specific deck or placed resource from which a card can be drawn.
     */
    List<Supplier<PlayableCard>> drawActionsRoutine = new ArrayList<>();

    /**
     * Constructs a PlayerTurnDrawState object with the specified controller and game.
     * Initializes the drawing actions for resource cards, gold cards, and placed resources and golds.
     *
     * @param controller The GameController managing the game flow.
     * @param thisGame   The current Game instance.
     */
    public PlayerTurnDrawState(GameController controller, Game thisGame) {
        super(controller, thisGame, "drawState");

        // Add drawing from resource cards deck
        this.drawActionsRoutine.add(
                () -> {
                    try {
                        PlayableCard drawnCard = GAME.drawFrom(GAME.getResourceCardsDeck());
                        GAME.peekFrom(GAME.getResourceCardsDeck());
                        return drawnCard;
                    } catch (EmptyDeckException ignored) {
                        return null;
                    }
                }
        );

        // Add drawing from gold cards deck
        this.drawActionsRoutine.add(
                () -> {
                    try {
                        PlayableCard drawnCard = GAME.drawFrom(GAME.getGoldCardsDeck());
                        GAME.peekFrom(GAME.getGoldCardsDeck());
                        return drawnCard;
                    } catch (EmptyDeckException ignored) {
                        return null;
                    }
                }
        );

        // Add drawing from placed resources
        for (int i = 0; i < GAME.getPlacedResources().length; i++) {
            final int index = i;
            this.drawActionsRoutine.add(
                    () -> {
                        try {
                            PlayableCard drawnCard = GAME.drawFrom(GAME.getPlacedResources(), index);
                            GAME.peekFrom(GAME.getResourceCardsDeck());
                            return drawnCard;
                        } catch (EmptyDeckException ignored) {
                            return null;
                        }
                    }
            );
        }

        // Add drawing from placed golds
        for (int i = 0; i < GAME.getPlacedGolds().length; i++) {
            final int index = i;
            this.drawActionsRoutine.add(
                    () -> {
                        try {
                            PlayableCard drawnCard = GAME.drawFrom(GAME.getPlacedGolds(), index);
                            GAME.peekFrom(GAME.getGoldCardsDeck());
                            return drawnCard;
                        } catch (EmptyDeckException ignored) {
                            return null;
                        }
                    }
            );
        }
    }

    /**
     * Handles the action of a player drawing a card from a specific deck during their turn.
     * Verifies the target player and the deck type, then adds the drawn card to the player's hand.
     *
     * @param target The player performing the draw action.
     * @param deck   The type of deck from which to draw ("RESOURCE" or "GOLD").
     * @throws UnexpectedPlayerException If the player attempting to draw is not the current player.
     * @throws UnknownStringException    If an unknown deck type string is provided.
     * @throws EmptyDeckException        If the specified deck is empty.
     */
    @Override
    public void drawFrom(InGamePlayer target, String deck) throws UnexpectedPlayerException,
            UnknownStringException, EmptyDeckException {
        if (!target.equals(GAME.getCurrentPlayer()))
            throw new UnexpectedPlayerException();

        PlayableCard drawnCard;

        if (deck.trim().equalsIgnoreCase("RESOURCE")) {
            drawnCard = GAME.drawFrom(GAME.getResourceCardsDeck());
            GAME.peekFrom(GAME.getResourceCardsDeck());
        } else if (deck.trim().equalsIgnoreCase("GOLD")) {
            drawnCard = GAME.drawFrom(GAME.getGoldCardsDeck());
            GAME.peekFrom(GAME.getGoldCardsDeck());
        } else
            throw new UnknownStringException();

        target.addCardToHand(drawnCard);

        transition();
    }

    /**
     * Handles the action of a player drawing a card from a specific placed deck during their turn.
     * Verifies the target player, deck type, and position, then adds the drawn card to the player's hand.
     *
     * @param target    The player performing the draw action.
     * @param whichType The type of placed deck from which to draw ("RESOURCE" or "GOLD").
     * @param position  The position within the placed deck from which to draw (0 or 1).
     * @throws UnexpectedPlayerException    If the player attempting to draw is not the current player.
     * @throws InvalidDeckPositionException If an invalid position within the placed deck is specified.
     * @throws UnknownStringException       If an unknown deck type string is provided.
     * @throws EmptyDeckException           If the specified placed deck is empty.
     */
    @Override
    public void drawFrom(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, InvalidDeckPositionException, UnknownStringException, EmptyDeckException {
        if (!target.equals(GAME.getCurrentPlayer()))
            throw new UnexpectedPlayerException();

        if (position != 0 && position != 1) {
            throw new InvalidDeckPositionException();
        }

        PlayableCard drawnCard;

        if (whichType.trim().equalsIgnoreCase("RESOURCE")) {
            drawnCard = GAME.drawFrom(GAME.getPlacedResources(), position);
            GAME.peekFrom(GAME.getResourceCardsDeck());
        } else if (whichType.trim().equalsIgnoreCase("GOLD")) {
            drawnCard = GAME.drawFrom(GAME.getPlacedGolds(), position);
            GAME.peekFrom(GAME.getGoldCardsDeck());
        } else
            throw new UnknownStringException();

        target.addCardToHand(drawnCard);

        transition();
    }

    /**
     * Handles the disconnection of a player during their turn.
     * Automatically tries to draw a card for the disconnected player following the implemented routine.
     *
     * @param target The player who disconnected.
     */
    @Override
    public void playerDisconnected(InGamePlayer target) {
        PlayableCard drawnCard;

        for (var actionFormat : this.drawActionsRoutine) {
            drawnCard = actionFormat.get();
            if (drawnCard != null) {
                target.addCardToHand(drawnCard);
                break;
            }
        }

        transition();
    }

    /**
     * Handles the transition to the next state after the current player's turn.
     * Updates the game state based on the current game conditions, eventually skipping disconnected players and
     * setting the final phase if the conditions are met.
     */
    @Override
    public void transition() {
        // If it was a disconnected player's turn, decrement final phase counter
        // and move to the next player's turn.

        GAME.nextPlayer();

        // Check if final phase conditions are met
        if (GAME.getFinalPhaseCounter() == -1 && GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()) {
            GAME.initializeFinalPhaseCounter();
            GAME.decreaseFinalPhaseCounter();
        }

        if (GAME.getFinalPhaseCounter() == 0) {
            GAME_CONTROLLER.setState(new VictoryCalculationState(GAME_CONTROLLER, GAME));
            GAME_CONTROLLER.getCurrentState().transition();
            return;
        }

        GAME_CONTROLLER.setState(new PlayerTurnPlayState(GAME_CONTROLLER, GAME));
    }
}
