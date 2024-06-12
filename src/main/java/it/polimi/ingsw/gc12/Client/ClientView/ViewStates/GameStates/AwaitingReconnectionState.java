package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;

import java.util.List;

public class AwaitingReconnectionState extends GameScreenState{

    private final ViewState LAST_STATE;

    public AwaitingReconnectionState(ViewState lastState) {
        this.LAST_STATE = lastState;
        TUICommands = List.of(
                "The game has been paused due to an insufficient number of players connected (1)",
                "If another player doesn't reconnect within 60 seconds, the game will end..."
        );
    }

    @Override
    public void executeState() {
        selectedView.awaitingScreen();
    }

    public void restoreScreenState(){
        //Never called at the moment, as an awaiting game has one and only one active player by definition, and
        // reconnecting means that there will be two active players, thus exiting the waiting state
    }

    @Override
    public void transition() {
        currentState = LAST_STATE;
    }
}
