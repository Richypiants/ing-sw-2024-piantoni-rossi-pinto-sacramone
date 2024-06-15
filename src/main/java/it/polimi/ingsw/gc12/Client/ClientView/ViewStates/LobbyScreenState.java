package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.CreateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.JoinLobbyCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.LeaveLobbyCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PickColorCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;

import java.util.UUID;

public class LobbyScreenState extends ViewState {

    public LobbyScreenState() {
    }

    @Override
    public void executeState() {
        selectedView.lobbiesScreen();
    }

    @Override
    public void updateNickname() {
        selectedView.showNickname();
    }

    @Override
    public void setNickname(String nickname) {
        CLIENT.requestToServer(new SetNicknameCommand(nickname));
    }

    @Override
    public void createLobby(int maxPlayers){
        CLIENT.requestToServer(new CreateLobbyCommand(maxPlayers));
    }

    @Override
    public void joinLobby(UUID lobbyUUID){
        CLIENT.requestToServer(new JoinLobbyCommand(lobbyUUID));
    }

    @Override
    public void selectColor(Color color) {
        CLIENT.requestToServer(new PickColorCommand(color));
    }

    @Override
    public void leaveLobby(){
        CLIENT.requestToServer(new LeaveLobbyCommand(false));
    }

    @Override
    public void quit() {
        if (CLIENT_CONTROLLER.VIEWMODEL.inRoom())
            CLIENT.requestToServer(new LeaveLobbyCommand(true));
        synchronized (CLIENT) {
            try {
                selectedView.quittingScreen();
                CLIENT.wait();
            } catch (InterruptedException e) {
                CLIENT_CONTROLLER.ERROR_LOGGER.log(e);
            }
        }
        super.quit();
    }
}
