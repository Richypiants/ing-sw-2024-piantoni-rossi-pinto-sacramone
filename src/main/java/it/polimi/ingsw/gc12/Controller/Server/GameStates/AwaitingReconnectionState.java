package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents the game state where the server is awaiting reconnection from any disconnected player.
 * If any player does not reconnect within a specified time, the game will proceed to victory calculation.
 */
public class AwaitingReconnectionState extends GameState {

    /** The previous state the game was performing, to return to after handling reconnection or timeout.
     */
    private final GameState previousState;

    /** Timer instance to handle the configured timespan to resume the game or terminate it. */
    private final Timer timer;

    /** Timer task to transition to victory calculation state when the timer expires */
    private final TimerTask terminateGame;

    /** Timeout duration in milliseconds after which the game will proceed to victory calculation. */
    private static final int TIMEOUT_GAME_ENDED = 60000;

    /**
     * Constructs an AwaitingReconnectionState object with the specified controller and game.
     * Initializes a timer to handle the timeout for reconnection.
     *
     * @param controller The GameController managing the game flow.
     * @param thisGame   The current Game instance.
     */
    public AwaitingReconnectionState(GameController controller, Game thisGame) {
        super(controller, thisGame, "awaitingReconnectionState");

        // Save the previous state to return to after reconnection or timeout
        this.previousState = GAME_CONTROLLER.getCurrentState();

        // Initialize a timer to transition to victory calculation state after timeout
        timer = new Timer(true);
        timer.schedule(terminateGame = new TimerTask() {
            @Override
            public void run() {
                // Transition to victory calculation state when timeout occurs
                GAME_CONTROLLER.setState(new VictoryCalculationState(GAME_CONTROLLER, GAME));
                GAME_CONTROLLER.getCurrentState().transition();
            }
        }, TIMEOUT_GAME_ENDED);
    }

    /**
     * Cancels the timer task when a player reconnects or the game is otherwise resolved.
     */
    public void cancelTimerTask(){
        timer.cancel();
        terminateGame.cancel();
    }

    /**
     * Handles the disconnection of a player during the reconnection phase.
     * Since nobody is connected: cancels the timer task, removes inactive session tracking, and destroys the game controller.
     *
     * @param target The player who disconnected.
     */
    @Override
    public void playerDisconnected(InGamePlayer target) {
        cancelTimerTask();
        for (var player : GAME.getPlayers())
            GameController.INACTIVE_SESSIONS.remove(player.getNickname());
        GameController.MODEL.destroyGameController(GAME_CONTROLLER);
    }

    /**
     * Handles the transition back to the previous state after reconnection or timeout.
     * Cancels the timer task and returns to the state that was active before awaiting reconnection.
     */
    @Override
    public void transition() {
        timer.cancel();
        terminateGame.cancel();
        GAME_CONTROLLER.setState(previousState);
    }
}
