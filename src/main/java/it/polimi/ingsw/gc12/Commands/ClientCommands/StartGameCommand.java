package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

/**
 * Represents a client command to start a game on the client side.
 * Implements the {@link ClientCommand} interface.
 */
public class StartGameCommand implements ClientCommand {

    private final ClientGame GAME_DTO;

    /**
     * Constructs a StartGameCommand with the specified game DTO.
     *
     * @param gameDTO The ClientGame DTO representing the game to start on the client side.
     */
    public StartGameCommand(ClientGame gameDTO) {
        this.GAME_DTO = gameDTO;
    }

    /**
     * Executes the command on the provided client controller, requesting to start the game using the provided game DTO.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.startGame(GAME_DTO);
    }
}
