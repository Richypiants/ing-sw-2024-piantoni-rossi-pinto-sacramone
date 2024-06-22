package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

import java.util.List;

/**
 * Represents the state of the game client where the game is paused awaiting reconnection from disconnected players.
 * Extends {@link GameScreenState}.
 */
public class AwaitingReconnectionState extends GameScreenState {

    private final ViewState LAST_STATE;

    /**
     * Constructs an AwaitingReconnectionState with the last active state before entering this state.
     *
     * @param lastState The last active state before entering the awaiting reconnection state.
     */
    public AwaitingReconnectionState(ViewState lastState) {
        this.LAST_STATE = lastState;
        TUICommands = List.of(
                "The game has been paused due to an insufficient number of players connected (1)",
                "If another player doesn't reconnect within 60 seconds, the game will end..."
        );
    }

    /**
     * Executes the behavior of the awaiting reconnection state by displaying the awaiting screen on the selected view.
     */
    @Override
    public void executeState() {
        ViewState.selectedView.awaitingScreen();
    }

    /**
     * Restores the screen state, though currently unused, as awaiting game states have only one active player by definition,
     * and reconnecting means there will be two active players, so you can't reconnect to a game which is in this state.
     */
    @Override
    public void restoreScreenState() {
        // Not currently used
    }

    /**
     * Transitions back to the last active state before entering the awaiting reconnection state.
     */
    @Override
    public void transition() {
        ViewState.currentState = LAST_STATE;
    }

    /**
     * Returns a string representation of the awaiting reconnection state.
     *
     * @return The string "awaiting state".
     */
    @Override
    public String toString() {
        return "awaiting state";
    }
}
