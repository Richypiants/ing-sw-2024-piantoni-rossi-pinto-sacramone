package it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Command;

public interface ClientCommand extends Command {

    void execute(ClientControllerInterface controller);
}
