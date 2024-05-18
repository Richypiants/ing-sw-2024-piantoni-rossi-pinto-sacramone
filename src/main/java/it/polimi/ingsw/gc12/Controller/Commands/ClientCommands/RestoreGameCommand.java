package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

public class RestoreGameCommand implements ClientCommand {

    private final ClientGame GAME_DTO;
    private final String CURRENT_STATE;

    public RestoreGameCommand(ClientGame gameDTO, String currentState) {
        this.GAME_DTO = gameDTO;
        this.CURRENT_STATE = currentState;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.restoreGame(GAME_DTO, CURRENT_STATE);
    }
}
