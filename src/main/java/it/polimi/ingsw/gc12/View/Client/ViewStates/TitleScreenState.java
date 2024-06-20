package it.polimi.ingsw.gc12.View.Client.ViewStates;

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

    @Override
    public String toString() {
        return "title screen";
    }
}
