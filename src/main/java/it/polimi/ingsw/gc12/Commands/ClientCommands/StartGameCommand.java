package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

public class StartGameCommand implements ClientCommand {

    private final ClientGame GAME_DTO;

    public StartGameCommand(ClientGame gameDTO) {
        this.GAME_DTO = gameDTO;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.startGame(GAME_DTO);
    }
}
