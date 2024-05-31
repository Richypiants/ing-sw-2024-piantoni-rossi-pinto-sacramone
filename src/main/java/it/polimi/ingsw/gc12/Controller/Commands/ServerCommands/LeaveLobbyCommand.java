package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class LeaveLobbyCommand implements ServerCommand {

    public LeaveLobbyCommand() {
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.leaveLobby(caller, false);
    }
}
