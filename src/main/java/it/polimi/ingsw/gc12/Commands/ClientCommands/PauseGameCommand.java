package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to pause the current game.
 * Implements the {@link ClientCommand} interface.
 */
public class PauseGameCommand implements ClientCommand {

    /**
     * Executes the command on the provided client controller, pausing the current game.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.pauseGame();
    }
}
