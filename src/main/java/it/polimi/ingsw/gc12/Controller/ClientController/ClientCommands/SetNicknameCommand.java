package it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

import java.util.Set;

public class SetNicknameCommand implements ClientCommand{

    private final String NICKNAME;

    public SetNicknameCommand(String nickname){
        this.NICKNAME = nickname;
    }

    @Override
    public void execute(ClientControllerInterface controller) throws Exception {
        controller.setNickname(NICKNAME);
    }
}
