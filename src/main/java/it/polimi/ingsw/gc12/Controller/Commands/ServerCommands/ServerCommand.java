package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public interface ServerCommand extends Command {

    void execute(NetworkSession caller, ServerControllerInterface controller);
}
