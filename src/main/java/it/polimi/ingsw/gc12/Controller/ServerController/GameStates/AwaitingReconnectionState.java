package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Game;

import java.util.Timer;
import java.util.TimerTask;

public class AwaitingReconnectionState extends GameState {

    private final GameState previousState;

    private final Timer timer;
    private final TimerTask terminateGame;

    private static final int TIMEOUT_GAME_ENDED = 60000;

    public AwaitingReconnectionState(GameController controller, Game thisGame) {
        super(controller, thisGame, "awaitingReconnectionState");

        synchronized (GAME_CONTROLLER.getCurrentState()) {
            this.previousState = GAME_CONTROLLER.getCurrentState();
        }

        timer = new Timer(true);
        timer.schedule(terminateGame = new TimerTask() {

            @Override
            public void run() {
                GAME_CONTROLLER.setState(new VictoryCalculationState(GAME_CONTROLLER, GAME));
                GAME_CONTROLLER.getCurrentState().transition();
            }
        }, TIMEOUT_GAME_ENDED);
    }

    public void recoverGame(){
        timer.cancel();
        terminateGame.cancel();
        GAME_CONTROLLER.setState(previousState);
    }

    public void cancelTimerTask(){
        timer.cancel();
        terminateGame.cancel();
    }

    //FIXME: merge with recoverGame?
    @Override
    public void transition() {
    }
}
