package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.Command;

public interface ClientCommand extends Command {

    void execute(ClientControllerInterface controller);
}
