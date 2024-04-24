package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.CreateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.JoinLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.LeaveLobbyCommand;
import it.polimi.ingsw.gc12.Controller.SetNicknameCommand;

import java.util.UUID;

public class LobbyScreenState extends ViewState {

    public LobbyScreenState() {
        ClientController.getInstance().view.lobbyScreen();
    }

    @Override
    public void setNickname(String nickname){
        try {
            ClientController.getInstance().serverConnection
                    .requestToServer(ClientController.getInstance().thisClient, new SetNicknameCommand(nickname));
        } catch (Exception e) {
            //printError();
        }
    }

    @Override
    public void updateNickname() {
        ClientController.getInstance().view.updateNickname();
    }

    @Override
    public void createLobby(int maxPlayers){
        try {
            ClientController.getInstance().serverConnection
                    .requestToServer(ClientController.getInstance().thisClient, new CreateLobbyCommand(maxPlayers));
        } catch (Exception e) {
            //printError();
        }
    }

    @Override
    public void joinLobby(UUID lobbyUUID){
        try {
            ClientController.getInstance().serverConnection
                    .requestToServer(ClientController.getInstance().thisClient, new JoinLobbyCommand(lobbyUUID));
        } catch (Exception e) {
            //printError();
        }
    }

    @Override
    public void leaveLobby(){
        try {
            ClientController.getInstance().serverConnection
                    .requestToServer(ClientController.getInstance().thisClient, new LeaveLobbyCommand());
        } catch (Exception e) {
            //printError();
        }
    }

    @Override
    public void returnToTitleScreen(){
        ClientController.getInstance().viewState = new TitleScreenState();
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new GameScreenState();
    }
}
