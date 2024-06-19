package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Commands.Command;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public interface ClientCommand extends Command {

    void execute(ClientControllerInterface controller);
}
