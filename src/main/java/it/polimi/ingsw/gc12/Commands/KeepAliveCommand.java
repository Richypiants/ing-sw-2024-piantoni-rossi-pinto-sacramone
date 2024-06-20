package it.polimi.ingsw.gc12.Commands;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class KeepAliveCommand implements ServerCommand, ClientCommand {

    public KeepAliveCommand() {
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.keepAlive(caller);
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.keepAlive();
    }
}




