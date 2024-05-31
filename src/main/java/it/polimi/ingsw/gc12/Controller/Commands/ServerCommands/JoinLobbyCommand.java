package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

import java.util.UUID;

public class JoinLobbyCommand implements ServerCommand {

    private final UUID lobbyUUID;

    public JoinLobbyCommand(UUID lobbyUUID) {
        this.lobbyUUID = lobbyUUID;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.joinLobby(caller, lobbyUUID);
    }
}
