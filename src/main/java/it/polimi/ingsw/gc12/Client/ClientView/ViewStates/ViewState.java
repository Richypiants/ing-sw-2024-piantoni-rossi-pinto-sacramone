package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIView;
import it.polimi.ingsw.gc12.Client.ClientView.View;

public abstract class ViewState {

    protected static View selectedView;
    protected static ViewState currentState;

    public static ViewState getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(ViewState currentState) {
        ViewState.currentState = currentState;
    }

    public static View getView() {
        return selectedView;
    }

    public static void setView(View view){
        selectedView = view;
    }

    public abstract void transition();
}
