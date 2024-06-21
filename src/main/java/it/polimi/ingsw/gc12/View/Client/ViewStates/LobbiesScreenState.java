package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.CreateLobbyCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.JoinLobbyCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.LeaveLobbyCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.PickColorCommand;
import it.polimi.ingsw.gc12.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;

import java.util.List;
import java.util.UUID;

public class LobbiesScreenState extends ViewState {

    public LobbiesScreenState() {
        TUICommands = List.of(
                "               '[createLobby | cl] <maxPlayers>' to create a new lobby,",
                "               '[joinLobby | jl] <lobbyUUID>' to join an existing lobby,",
                "               '[setNickname | sn] <newNickname>' to change your own nickname,",
                "               '[selectColor | sc] <color>' to choose a color among the available ones,",
                "               '[leaveLobby | ll]' to leave the lobby you are currently in,",
                "               '[quit]' to go back to title screen."
        );
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
        if (!nickname.isEmpty() && nickname.length() <= 10)
            CLIENT.requestToServer(new SetNicknameCommand(nickname));
        else
            selectedView.printError(new IllegalArgumentException("The entered nickname is longer than 10 characters or is empty! Retry..."));
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
        new Thread(() -> {
            synchronized (CLIENT) {
                try {
                    if (CLIENT_CONTROLLER.VIEWMODEL.inRoom())
                        CLIENT.requestToServer(new LeaveLobbyCommand(true));
                    selectedView.quittingScreen();
                    //Notified by CLIENT.requestToServer() function, which gets executed in another thread
                    CLIENT.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //Should never happen
                }
            }
            super.quit();
        }).start();
    }

    @Override
    public String toString() {
        return "lobbies screen";
    }
}
