package it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class KeepAliveCommand implements ServerCommand {

    public KeepAliveCommand() {
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.keepAlive(caller);
    }
}




