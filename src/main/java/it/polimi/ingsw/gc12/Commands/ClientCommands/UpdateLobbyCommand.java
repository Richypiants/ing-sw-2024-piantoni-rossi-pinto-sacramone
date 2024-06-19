package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.Lobby;

public class UpdateLobbyCommand implements ClientCommand {

    private final Lobby LOBBY;

    public UpdateLobbyCommand(Lobby lobby) {
        this.LOBBY = lobby;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.updateLobby(LOBBY);
    }
}
