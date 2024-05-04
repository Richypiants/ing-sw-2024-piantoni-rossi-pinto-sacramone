package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class GameTransitionCommand implements ClientCommand{

    public GameTransitionCommand() {
    }

    public void execute(ClientControllerInterface clientController){
        clientController.transition();
    }

}
