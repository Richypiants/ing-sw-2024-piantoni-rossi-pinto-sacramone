package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class PauseGameCommand implements ClientCommand {

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.pauseGame();
    }
}
