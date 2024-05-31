package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class CreatePlayerCommand implements ServerCommand {

    private final String NICKNAME;

    public CreatePlayerCommand(String nickname) {
        NICKNAME = nickname;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.createPlayer(caller, NICKNAME);
    }
}
