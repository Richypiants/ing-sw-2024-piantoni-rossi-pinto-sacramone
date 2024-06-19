package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

public class StartGameCommand implements ClientCommand {

    private final ClientGame gameDTO;

    public StartGameCommand(ClientGame gameDTO) {
        this.gameDTO = gameDTO;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.startGame(gameDTO);
    }
}
