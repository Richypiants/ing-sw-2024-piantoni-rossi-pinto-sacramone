package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Client.ClientView.View;

public abstract class ViewState {

    protected static View selectedView;
    protected static ViewState currentState;

    public static ViewState getCurrentState() {
        return currentState;
    }

    public void keyPressed() {
        //throw new ForbiddenActionException();
    }

    public void setNickname(String nickname) {
        //throw new ForbiddenActionException();
    }

    public abstract void transition();
}
