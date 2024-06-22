package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.CreateLobbyCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.JoinLobbyCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.LeaveLobbyCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.PickColorCommand;
import it.polimi.ingsw.gc12.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;

import java.util.List;
import java.util.UUID;

/**
 * Represents the lobbies screen state of the client-side view.
 * Extends {@link ViewState}.
 */
public class LobbiesScreenState extends ViewState {

    /**
     * Constructs a new LobbiesScreenState instance.
     * Initializes {@link #TUICommands} with available commands for this state.
     */
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

    /**
     * Executes the behavior of the lobbies screen state by displaying the lobbies screen on the selected view.
     */
    @Override
    public void executeState() {
        selectedView.lobbiesScreen();
    }

    /**
     * Updates the nickname display on the selected view.
     */
    @Override
    public void updateNickname() {
        selectedView.showNickname();
    }

    /**
     * Requests the server to set the specified nickname.
     *
     * @param nickname The new nickname to set.
     */
    @Override
    public void setNickname(String nickname) {
        if (!nickname.isEmpty() && nickname.length() <= 10)
            CLIENT.requestToServer(new SetNicknameCommand(nickname));
        else
            selectedView.printError(new IllegalArgumentException("The entered nickname is longer than 10 characters or is empty! Retry..."));
    }

    /**
     * Requests the server to create a new lobby with the specified maximum number of players.
     *
     * @param maxPlayers The maximum number of players for the new lobby.
     */
    @Override
    public void createLobby(int maxPlayers){
        CLIENT.requestToServer(new CreateLobbyCommand(maxPlayers));
    }

    /**
     * Requests the server to join the lobby identified by the specified UUID.
     *
     * @param lobbyUUID The UUID of the lobby to join.
     */
    @Override
    public void joinLobby(UUID lobbyUUID){
        CLIENT.requestToServer(new JoinLobbyCommand(lobbyUUID));
    }

    /**
     * Requests the server to select the specified color.
     *
     * @param color The color to select.
     */
    @Override
    public void selectColor(Color color) {
        CLIENT.requestToServer(new PickColorCommand(color));
    }

    /**
     * Requests the server to leave the current lobby.
     */
    @Override
    public void leaveLobby(){
        CLIENT.requestToServer(new LeaveLobbyCommand(false));
    }

    /**
     * Initiates the process of quitting the application.
     * If the client is currently in a lobby, first requests to leave the lobby.
     * Displays the quitting screen on the selected view and waits for confirmation from the server.
     * Upon confirmation, resets the client and transitions to the title screen state.
     */
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

    /**
     * Returns a string representation of the lobbies screen state.
     *
     * @return The string "lobbies screen".
     */
    @Override
    public String toString() {
        return "lobbies screen";
    }
}

