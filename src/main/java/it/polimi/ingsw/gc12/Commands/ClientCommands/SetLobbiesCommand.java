package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.Lobby;

import java.util.Map;
import java.util.UUID;

public class SetLobbiesCommand implements ClientCommand {

    private final Map<UUID, Lobby> LOBBIES;

    public SetLobbiesCommand(Map<UUID, Lobby> lobbies) {
        this.LOBBIES = lobbies;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.setLobbies(LOBBIES);
    }
}
