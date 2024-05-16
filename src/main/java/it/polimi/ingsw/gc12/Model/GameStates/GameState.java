package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
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
    public InGamePlayer getCurrentPlayer() {
        return GAME.getPlayers().get(currentPlayer);
    }

    /**
    Increases the current player counter, making it point to the next player, increasing the turn after everyone
    has played in the current turn
     */
    public void nextPlayer() {
        if (currentPlayer == GAME.getPlayers().size()-1)
            GAME.increaseTurn();

        do {
            currentPlayer = (currentPlayer + 1) % GAME.getPlayers().size();
            if(counter != -1)
                counter--;
            if(counter == 0)
                //There's no need to find another active player, since the game is ended.
                break;
        } while(!GAME.getPlayers().get(currentPlayer).isActive());
    }

    public void pickObjective(InGamePlayer target, ObjectiveCard objective)
            throws ForbiddenActionException, AlreadySetCardException, CardNotInHandException {
        throw new ForbiddenActionException();
    }

    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> position, PlayableCard card, Side playedSide)
            throws ForbiddenActionException, UnexpectedPlayerException, CardNotInHandException,
            NotEnoughResourcesException, InvalidCardPositionException {
        throw new ForbiddenActionException();
    }

    public void drawFrom(InGamePlayer target, String deck) throws ForbiddenActionException, UnexpectedPlayerException,
            UnknownStringException, EmptyDeckException {
        throw new ForbiddenActionException();
    }

    //FIXME: change in UML
    public void drawFrom(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, ForbiddenActionException, InvalidDeckPositionException,
            UnknownStringException, EmptyDeckException {
        throw new ForbiddenActionException();
    }

    public void playerDisconnected(InGamePlayer target){
        //NOTHING TO DO?
    }

    public void transition() {
        persistence();
    }

    private void persistence() {
        //TODO: implement serialization here
    }
}
