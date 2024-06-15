package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

public abstract class GameState { //TODO: make all exceptions extends RuntimeException so that you can cancel them from here

    protected final GameController GAME_CONTROLLER;
    protected final Game GAME;

    protected String state;

    public GameState(GameController controller, Game thisGame, String state) {
        this.GAME_CONTROLLER = controller;
        this.GAME = thisGame;
        this.state = state;
    }

    public String getStringEquivalent(){
        return state;
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

    public void drawFrom(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, ForbiddenActionException, InvalidDeckPositionException,
            UnknownStringException, EmptyDeckException {
        throw new ForbiddenActionException();
    }

    public abstract void playerDisconnected(InGamePlayer target);

    public abstract void transition();
}
