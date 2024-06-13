package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class LeaveLobbyCommand implements ServerCommand {

    private final boolean IS_INACTIVE;

    public LeaveLobbyCommand(boolean isInactive) {
        this.IS_INACTIVE = isInactive;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.leaveLobby(caller, IS_INACTIVE);
    }
}
