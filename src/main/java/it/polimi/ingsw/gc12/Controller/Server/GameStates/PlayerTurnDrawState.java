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

public class PlayerTurnDrawState extends GameState {

    /*
    * LAMBDA for doing the stated drawing action
     */
    List<Supplier<PlayableCard>> drawActionsRoutine = new ArrayList<>();

    public PlayerTurnDrawState(GameController controller, Game thisGame) {
        super(controller, thisGame, "drawState");

        this.drawActionsRoutine.add(
                () -> {
                    try {
                        PlayableCard drawnCard = GAME.drawFrom(GAME.getResourceCardsDeck());
                        GAME.peekFrom(GAME.getResourceCardsDeck());
                        return drawnCard;
                    } catch (EmptyDeckException ignored) {
                        //FIXME: alla luce del fatto che il catch vuoto di una exception negli stati iniziali faceva crashare la
                        // playerDisconnected, forse tutti i return null vanno messi qui dentro perchè altrimenti non vengono eseguiti?
                        // Magari potrebbe essere stato questo a far crashare il game ieri?
                        return null;
                    }
                }
        );

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

        // Add drawing actions for placed resources
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

        // Add drawing actions for placed golds
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

    @Override
    public void transition() {
        //REMINDER: se è stato completato il turno di un giocatore disconnesso,
        // il contatore dei turni rimanenti nel caso di finalPhase viene decrementato dalla nextPlayer().

        GAME.nextPlayer();

        //Is final condition satisfied check
        if (GAME.getFinalPhaseCounter() == -1 && GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()) {
            GAME.initializeFinalPhaseCounter();
            GAME.decreaseFinalPhaseCounter();
        }

        //TODO: segnalare ai giocatori connessi che si stanno giocando i turni finali,
        // attraverso la GameTransitionCommand [Un campo Boolean, il # di Turno in cui finirà la partita,
        // il contatore decrementato?

        if (GAME.getFinalPhaseCounter() == 0) {
            GAME_CONTROLLER.setState(new VictoryCalculationState(GAME_CONTROLLER, GAME));
            GAME_CONTROLLER.getCurrentState().transition();
            return;
        }

        GAME_CONTROLLER.setState(new PlayerTurnPlayState(GAME_CONTROLLER, GAME));
    }
}
