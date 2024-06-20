package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

import java.util.UUID;

public class JoinLobbyCommand implements ServerCommand {

    private final UUID LOBBY_UUID;

    public JoinLobbyCommand(UUID lobbyUUID) {
        this.LOBBY_UUID = lobbyUUID;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.joinLobby(caller, LOBBY_UUID);
    }
}
