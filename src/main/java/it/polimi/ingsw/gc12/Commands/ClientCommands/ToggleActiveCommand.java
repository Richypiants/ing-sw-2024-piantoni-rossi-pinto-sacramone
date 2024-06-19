package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class ToggleActiveCommand implements ClientCommand {

    private final String NICKNAME;

    public ToggleActiveCommand(String nickname) {
        this.NICKNAME = nickname;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.toggleActive(NICKNAME);
    }
}
