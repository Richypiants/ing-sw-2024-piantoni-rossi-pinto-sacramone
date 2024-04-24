package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Client.ClientView.View;

import java.util.UUID;

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

    public void setNickname(String nickname){
        //"throw Exception"
    }

    public void createLobby(int maxPlayers){

    }

    public void joinLobby(UUID lobbyUUID){

    }

    public void leaveLobby(){

    }

    public void returnToTitleScreen(){

    }

    public void updateNickname() {

    }

    public abstract void transition();
}
