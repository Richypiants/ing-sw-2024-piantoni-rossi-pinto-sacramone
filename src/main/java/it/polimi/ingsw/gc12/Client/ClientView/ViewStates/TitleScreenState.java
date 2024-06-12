package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

public class TitleScreenState extends ViewState {

    public TitleScreenState() {
    }

    @Override
    public void executeState() {
        selectedView.titleScreen();
    }

    @Override
    public void keyPressed() {
        currentState = new ConnectionSetupState();
        currentState.executeState();
    }

    //TODO: quit anche qui?
}
