package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class SetNicknameCommand implements ServerCommand, ClientCommand {

    private final String NICKNAME;

    public SetNicknameCommand(String nickname){
        this.NICKNAME = nickname;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.setNickname(caller, NICKNAME);
    }

    @Override
    public void execute(ClientControllerInterface controller) throws Exception {
        controller.setNickname(NICKNAME);
    }
}
