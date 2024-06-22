package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to toggle the activity status of a player.
 * Implements the {@link ClientCommand} interface.
 */
public class ToggleActiveCommand implements ClientCommand {

    private final String NICKNAME;

    /**
     * Constructs a ToggleActiveCommand with the nickname of the player whose activity status is to be toggled.
     *
     * @param nickname The nickname of the player whose activity status is to be toggled.
     */
    public ToggleActiveCommand(String nickname) {
        this.NICKNAME = nickname;
    }

    /**
     * Executes the command on the provided client controller, requesting to toggle the activity status
     * of the player with the given nickname.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.toggleActive(NICKNAME);
    }
}
