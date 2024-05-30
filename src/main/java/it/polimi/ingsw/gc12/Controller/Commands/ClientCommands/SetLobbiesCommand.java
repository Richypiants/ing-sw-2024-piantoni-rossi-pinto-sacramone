package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.Room;

import java.util.Map;
import java.util.UUID;

public class SetLobbiesCommand implements ClientCommand {

    private final Map<UUID, Room> LOBBIES;

    public SetLobbiesCommand(Map<UUID, Room> lobbies) {
        this.LOBBIES = lobbies;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.setLobbies(LOBBIES);
    }
}
