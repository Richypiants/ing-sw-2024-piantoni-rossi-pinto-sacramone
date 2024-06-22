package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to change the activity status of a player.
 * Implements the {@link ClientCommand} interface.
 */
public class SetPlayerActivityCommand implements ClientCommand {

    private final String NICKNAME;
    private final boolean IS_ACTIVE;

    /**
     * Constructs a SetPlayerActivityCommand with the nickname of the player whose activity status is to be changes.
     *
     * @param nickname The nickname of the player whose activity status is to be toggled.
     * @param isActive The new value of player activity status.
     */
    public SetPlayerActivityCommand(String nickname, boolean isActive) {
        this.NICKNAME = nickname;
        this.IS_ACTIVE = isActive;
    }

    /**
     * Executes the command on the provided client controller, requesting to change the activity status
     * of the player with the given nickname to the given activity status.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.setPlayerActivity(NICKNAME, IS_ACTIVE);
    }
}
