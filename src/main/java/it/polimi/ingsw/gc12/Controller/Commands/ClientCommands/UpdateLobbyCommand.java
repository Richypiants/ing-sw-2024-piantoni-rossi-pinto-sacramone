package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.GameLobby;

import java.util.UUID;

public class UpdateLobbyCommand implements ClientCommand {

    private final UUID LOBBY_UUID;
    private final GameLobby LOBBY;

    public UpdateLobbyCommand(UUID lobbyUUID, GameLobby lobby) {
        this.LOBBY_UUID = lobbyUUID;
        this.LOBBY = lobby;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.updateLobby(LOBBY_UUID, LOBBY);
    }
}
