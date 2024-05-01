package it.polimi.ingsw.gc12.Controller.Commands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class KeepAliveCommand implements ServerCommand, ClientCommand {

    public KeepAliveCommand() {
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) {
        serverController.keepAlive(caller);
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.keepAlive();
    }
}




