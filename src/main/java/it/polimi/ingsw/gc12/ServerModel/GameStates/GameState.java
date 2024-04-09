package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

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

    public static void createLobby(Player player, int maxPlayers) throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void setNickname(Player player, String nickname) throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void joinLobby(Player player, UUID lobbyUUID) throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void leaveLobby(Player player) throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void placeCommonCards() throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void generateInitialCard() throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void placeInitialCard(InGamePlayer player, Side side) throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void drawInitialHand() throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public Map<InGamePlayer, ArrayList<ObjectiveCard>> generateObjectiveChoice() throws ForbiddenActionException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void pickObjective(InGamePlayer player, ObjectiveCard objective)
            throws ForbiddenActionException, AlreadySetCardException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> position, PlayableCard card, Side side)
            throws ForbiddenActionException, UnexpectedPlayerException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void drawFrom(InGamePlayer target, String deck) throws ForbiddenActionException, UnexpectedPlayerException {
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    //FIXME: change in UML
    public void drawFrom(InGamePlayer target, String whichType, int position)
            throws ForbiddenActionException, InvalidPositionException,
            UnknownStringException{
        throw new ForbiddenActionException("Cannot execute this action while...");
    }

    public void transition() {
        persistence();
    }

    private void persistence() {
        //TODO: implement serialization here
    }
}
