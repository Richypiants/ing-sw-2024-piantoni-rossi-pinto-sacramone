package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

import java.util.List;

public class AwaitingReconnectionState extends GameScreenState{

    private final ViewState LAST_STATE;

    public AwaitingReconnectionState(ViewState lastState) {
        this.LAST_STATE = lastState;
        TUICommands = List.of(
                "The game has been paused due to an insufficient number of players connected (1)",
                "If another player doesn't reconnect within 60 seconds, the game will end"
        );
    }

    @Override
    public void executeState() {
        super.executeState();
        //TODO: Maybe print only the messages that notifies this particular situation?
        ClientController.getInstance().view.gameScreen();
    }

    public void restoreScreenState(){
        //Never called at the moment
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = LAST_STATE;
    }
}
