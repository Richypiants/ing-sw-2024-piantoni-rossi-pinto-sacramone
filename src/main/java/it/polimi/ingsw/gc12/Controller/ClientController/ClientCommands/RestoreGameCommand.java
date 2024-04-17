package it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

public class RestoreGameCommand implements ClientCommand {

    private final ClientGame GAME_DTO;

    public RestoreGameCommand(ClientGame gameDTO) {
        this.GAME_DTO = gameDTO;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.restoreGame(GAME_DTO);
    }
}
