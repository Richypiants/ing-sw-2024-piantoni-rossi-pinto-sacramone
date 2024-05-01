package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

import java.util.UUID;

public class StartGameCommand implements ClientCommand {

    private final UUID LOBBY_UUID;
    private final ClientGame gameDTO;

    public StartGameCommand(UUID lobbyUUID, ClientGame gameDTO) {
        this.LOBBY_UUID = lobbyUUID;
        this.gameDTO = gameDTO;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.startGame(LOBBY_UUID, gameDTO);
    }
}
