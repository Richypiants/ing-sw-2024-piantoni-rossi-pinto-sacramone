package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

public class ConnectToServerScreenState extends ViewState {

    public ConnectToServerScreenState() {
        selectedView.chooseNicknameScreen();
    }

    @Override
    public void setNickname(String nickname) {
        //ClientController.getInstance().serverConnection
        //        .requestToServer(ClientController.getInstance().thisClient, new CreatePlayerCommand(nickname));
        selectedView.connectToServerScreen();
        transition();
    }

    @Override
    public void transition() {
        currentState = new LobbyScreenState();
    }
}
