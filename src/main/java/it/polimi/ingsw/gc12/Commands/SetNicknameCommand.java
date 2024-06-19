package it.polimi.ingsw.gc12.Commands;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class SetNicknameCommand implements ServerCommand, ClientCommand {

    private final String NICKNAME;

    public SetNicknameCommand(String nickname){
        this.NICKNAME = nickname;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.setNickname(caller, NICKNAME);
    }

    @Override
    public void execute(ClientControllerInterface controller) {
        controller.setNickname(NICKNAME);
    }
}
