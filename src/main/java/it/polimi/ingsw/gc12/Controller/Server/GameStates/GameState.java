package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

/**
 * The abstract base class representing a state in the game.
 * Each specific game state should extend this class and implement its abstract methods.
 */
public abstract class GameState {

    /** The GameController instance managing the current game. */
    protected final GameController GAME_CONTROLLER;
    /** The Game instance associated with this state. */
    protected final Game GAME;
    /** A string representing the name or type of this game state. */
    protected String state;

    /**
     * Constructs a new GameState.
     *
     * @param controller The GameController managing the game.
     * @param thisGame The Game instance associated with this state.
     * @param state A string representing the name or type of this game state.
     */
    public GameState(GameController controller, Game thisGame, String state) {
        this.GAME_CONTROLLER = controller;
        this.GAME = thisGame;
        this.state = state;
    }

    /**
     * Gets the string representation of this game state.
     *
     * @return The string equivalent of this state.
     */
    public String getStringEquivalent(){
        return state;
    }

    /**
     * Picks an objective card for the specified player.
     *
     * @param target The player picking the objective card.
     * @param objective The objective card to be picked.
     * @throws ForbiddenActionException If the action is not allowed in the current state.
     * @throws AlreadySetCardException If the player has already set an objective card.
     * @throws CardNotInHandException If the specified card is not in the player's hand.
     */
    public void pickObjective(InGamePlayer target, ObjectiveCard objective)
            throws ForbiddenActionException, AlreadySetCardException, CardNotInHandException {
        throw new ForbiddenActionException();
    }

    /**
     * Places a card at the specified position for the specified player.
     *
     * @param target The player placing the card.
     * @param position The position where the card is to be placed.
     * @param card The card to be placed.
     * @param playedSide The side of the card being played.
     * @throws ForbiddenActionException If the action is not allowed in the current state.
     * @throws UnexpectedPlayerException If it is not the specified player's turn.
     * @throws CardNotInHandException If the specified card is not in the player's hand.
     * @throws NotEnoughResourcesException If the player does not have enough resources to place the card.
     * @throws InvalidCardPositionException If the specified position is invalid for placing the card.
     */
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> position, PlayableCard card, Side playedSide)
            throws ForbiddenActionException, UnexpectedPlayerException, CardNotInHandException,
            NotEnoughResourcesException, InvalidCardPositionException {
        throw new ForbiddenActionException();
    }

    /**
     * Draws a card from the specified deck for the specified player.
     *
     * @param target The player drawing the card.
     * @param deck The name of the deck to draw from.
     * @throws ForbiddenActionException If the action is not allowed in the current state.
     * @throws UnexpectedPlayerException If it is not the specified player's turn.
     * @throws UnknownStringException If the specified deck does not exist.
     * @throws EmptyDeckException If the specified deck is empty.
     */
    public void drawFrom(InGamePlayer target, String deck) throws ForbiddenActionException, UnexpectedPlayerException,
            UnknownStringException, EmptyDeckException {
        throw new ForbiddenActionException();
    }

    /**
     * Draws a card from the specified deck and position for the specified player.
     *
     * @param target The player drawing the card.
     * @param whichType The type of the deck to draw from.
     * @param position The position of the card in the deck to draw.
     * @throws ForbiddenActionException If the action is not allowed in the current state.
     * @throws UnexpectedPlayerException If it is not the specified player's turn.
     * @throws InvalidDeckPositionException If the specified position is invalid.
     * @throws UnknownStringException If the specified deck type does not exist.
     * @throws EmptyDeckException If there are no cards at the specified position.
     */
    public void drawFrom(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, ForbiddenActionException, InvalidDeckPositionException,
            UnknownStringException, EmptyDeckException {
        throw new ForbiddenActionException();
    }

    /**
     * Handles the disconnection of a player.
     *
     * @param target The player who has disconnected.
     */
    public abstract void playerDisconnected(InGamePlayer target);

    /**
     * Transitions to the next state.
     */
    public abstract void transition();
}
