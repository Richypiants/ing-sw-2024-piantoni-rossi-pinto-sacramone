package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.GameTransitionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;

import java.util.Timer;
import java.util.TimerTask;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class AwaitingReconnectionState extends GameState {

    private final GameState previousState;
    private final TimerTask terminateGame;
    private static final int TIMEOUT_GAME_ENDED = 60000;

    public AwaitingReconnectionState(Game thisGame) {
        super(thisGame, thisGame.getCurrentState().currentPlayer, thisGame.getCurrentState().finalPhaseCounter, "awaitingReconnectionState");

        synchronized (GAME.getCurrentState()) {
            this.previousState = GAME.getCurrentState();
        }

        Timer timer = new Timer(true);
        timer.schedule(terminateGame = new TimerTask() {

            @Override
            public void run() {
                GAME.setState(new VictoryCalculationState(GAME, currentPlayer, finalPhaseCounter));
                GAME.getCurrentState().transition();
            }
        }, TIMEOUT_GAME_ENDED);
    }

    public void recoverGame(){
        terminateGame.cancel();
        GAME.setState(previousState);

        for(InGamePlayer player : GAME.getActivePlayers()){
            ServerController.getInstance().requestToClient(
                    keyReverseLookup(ServerController.getInstance().players, player::equals),
                    new GameTransitionCommand(GAME.getCurrentState().currentPlayer, GAME.getCurrentState().finalPhaseCounter)
            );
        }
    }


}
