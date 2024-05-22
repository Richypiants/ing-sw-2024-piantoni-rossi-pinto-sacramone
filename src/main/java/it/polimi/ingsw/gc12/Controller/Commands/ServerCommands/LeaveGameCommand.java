package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.VirtualClient;

public class LeaveGameCommand implements ServerCommand {

    public LeaveGameCommand() {
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) {
        serverController.leaveGame(caller);
    }

}
