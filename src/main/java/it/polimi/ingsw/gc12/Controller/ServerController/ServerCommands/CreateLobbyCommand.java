package it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class CreateLobbyCommand implements ServerCommand {

    private final int MAX_PLAYERS;

    public CreateLobbyCommand(int maxPlayers) {
        MAX_PLAYERS = maxPlayers;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.createLobby(caller, MAX_PLAYERS);
    }
}
