package it.polimi.ingsw.gc12.Controller.Commands;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class KeepAliveCommand implements ServerCommand {

    public KeepAliveCommand() {
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.keepAlive(caller);
    }
}




