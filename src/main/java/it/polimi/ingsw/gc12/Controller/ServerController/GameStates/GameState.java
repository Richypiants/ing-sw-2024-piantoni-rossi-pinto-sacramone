package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.GameTransitionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public abstract class GameState { //TODO: make all exceptions extends RuntimeException so that you can cancel them from here
    protected final Game GAME;
    /**
    The index which points to the player which is playing right now (-1 when the game is in the setup phase)
     */
    protected int currentPlayer;
    protected int finalPhaseCounter;

    protected String state;


    public GameState(Game thisGame, int currentPlayer, int finalPhaseCounter, String state) {
        this.GAME = thisGame;
        this.currentPlayer = currentPlayer;
        this.finalPhaseCounter = finalPhaseCounter;
        this.state = state;
    }

    /**
    Returns the player who is currently playing
     */
    public InGamePlayer getCurrentPlayer() {
        if( currentPlayer != -1)
            return GAME.getPlayers().get(currentPlayer);
        //FIXME: Maybe not a null but something else?
        return null;
    }

    public String getStringEquivalent(){
        return state;
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
            if(finalPhaseCounter != -1)
                finalPhaseCounter--;
            if(finalPhaseCounter == 0)
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
        //NOTHING TO DO? Can become abstract! (What to do in AwaitingReconnectionState... empty method)
    }

    public void transition() {
        persistence();
    }

    protected static void notifyTransition(ArrayList<InGamePlayer> activePlayers, int turnNumber, int indexOfCurrentPlayer) {
        for (var targetPlayer : activePlayers) {
            //TODO: manage exceptions
            try {
                VirtualClient target = keyReverseLookup(GameController.players, targetPlayer::equals);

                GameController.requestToClient(
                        target,
                        new GameTransitionCommand(
                                turnNumber,
                                indexOfCurrentPlayer
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void persistence() {
        //TODO: implement serialization here
    }
}
