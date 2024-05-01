package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.GameScreenState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.CreateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.JoinLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.LeaveLobbyCommand;
import it.polimi.ingsw.gc12.Controller.SetNicknameCommand;

import java.util.UUID;

import static java.lang.Thread.sleep;

public class LobbyScreenState extends ViewState {

    public LobbyScreenState() {
    }

    @Override
    public void executeState() {
        /*try {
            sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        ClientController.getInstance().view.lobbyScreen();
    }

    @Override
    public void setNickname(String nickname){
        ClientController.getInstance().requestToServer(new SetNicknameCommand(nickname));
    }

    @Override
    public void updateNickname() {
        ClientController.getInstance().view.updateNickname();
    }

    @Override
    public void createLobby(int maxPlayers){
        ClientController.getInstance().requestToServer(new CreateLobbyCommand(maxPlayers));
    }

    @Override
    public void joinLobby(UUID lobbyUUID){
        ClientController.getInstance().requestToServer(new JoinLobbyCommand(lobbyUUID));
    }

    @Override
    public void leaveLobby(){
        ClientController.getInstance().requestToServer(new LeaveLobbyCommand());
    }

    @Override
    public void returnToTitleScreen(){
        ClientController.getInstance().viewState = new TitleScreenState();
        ClientController.getInstance().viewState.executeState();
    }

    @Override
    public void startGame() {
        ClientController.getInstance().viewState = new GameScreenState();
        ClientController.getInstance().viewState.executeState();
    }

    @Override
    public void quit() {
        if (ClientController.getInstance().currentLobbyOrGame != null)
            ClientController.getInstance().requestToServer(new LeaveLobbyCommand());
        super.quit();
    }
}
