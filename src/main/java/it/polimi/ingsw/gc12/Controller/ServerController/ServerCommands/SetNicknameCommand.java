package it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class SetNicknameCommand implements ServerCommand {

    private final String NICKNAME;

    public SetNicknameCommand(String nickname) {
        this.NICKNAME = nickname;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.setNickname(caller, NICKNAME);
    }
}
