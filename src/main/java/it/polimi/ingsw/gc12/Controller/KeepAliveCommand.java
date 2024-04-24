package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class KeepAliveCommand implements ServerCommand, ClientCommand {

    public KeepAliveCommand() {
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.keepAlive(caller);
    }

    @Override
    public void execute(ClientControllerInterface clientController) throws Exception {
        clientController.keepAlive();
    }
}




