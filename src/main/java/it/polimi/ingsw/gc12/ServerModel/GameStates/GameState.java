package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

public abstract class GameState { //TODO: make all exceptions extends RuntimeException so that you can cancel them from here
    protected final Game GAME;
    /**
    The index which points to the player which is playing right now (starting from 0 when the game starts)
     */
    protected int currentPlayer;
    protected int counter;

    public GameState(Game thisGame, int currentPlayer, int counter) {
        this.GAME = thisGame;
        this.currentPlayer = currentPlayer;
        this.counter = counter;
    }

    /**
    Returns the player who is currently playing
     */
    public Player getCurrentPlayer() {
        return GAME.getPlayers().get(currentPlayer);
    }

    /**
    Increases the current player counter, making it point to the next player, increasing the turn after everyone
    has played in the current turn
     */
    public void nextPlayer() {
        if (currentPlayer == GAME.getPlayers().size()) {
            GAME.increaseTurn();
        }
        while (!GAME.getPlayers().get(currentPlayer).isActive())
            currentPlayer = (currentPlayer + 1) % GAME.getPlayers().size();
    }

    public void placeInitialCard(InGamePlayer target, Side playedSide) throws ForbiddenActionException,
            CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {
        throw new ForbiddenActionException();
    }

    public void pickObjective(InGamePlayer target, ObjectiveCard objective)
            throws ForbiddenActionException, AlreadySetCardException, CardNotInHandException {
        throw new ForbiddenActionException();
    }

    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> position, PlayableCard card, Side side)
            throws ForbiddenActionException, UnexpectedPlayerException, CardNotInHandException,
            NotEnoughResourcesException, InvalidCardPositionException {
        throw new ForbiddenActionException();
    }

    public void drawFrom(InGamePlayer target, String deck) throws ForbiddenActionException, UnexpectedPlayerException,
            UnknownStringException {
        throw new ForbiddenActionException();
    }

    //FIXME: change in UML
    public void drawFrom(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, ForbiddenActionException, InvalidDeckPositionException,
            UnknownStringException{
        throw new ForbiddenActionException();
    }

    public void transition() {
        persistence();
    }

    private void persistence() {
        //TODO: implement serialization here
    }
}
