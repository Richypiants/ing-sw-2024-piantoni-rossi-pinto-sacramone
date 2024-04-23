package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

public class TitleScreenState extends ViewState {

    public TitleScreenState() {
        selectedView.titleScreen();
        transition();
    }

    @Override
    public void transition() {
        currentState = new ConnectToServerScreenState();
    }
}
