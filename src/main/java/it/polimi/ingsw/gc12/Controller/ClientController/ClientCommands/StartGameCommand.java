package it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.GameLobby;

import java.util.UUID;

public class StartGameCommand implements ClientCommand {

    private final UUID LOBBY_UUID;
    private final GameLobby lobby;

    public StartGameCommand(UUID lobbyUUID, GameLobby lobby) {
        this.LOBBY_UUID = lobbyUUID;
        this.lobby = lobby;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.startGame(LOBBY_UUID, lobby);
    }
}
