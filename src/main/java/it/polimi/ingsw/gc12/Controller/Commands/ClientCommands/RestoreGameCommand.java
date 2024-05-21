package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RestoreGameCommand implements ClientCommand {

    private final UUID GAME_UUID;
    private final ClientGame GAME_DTO;
    private final String CURRENT_STATE;
    private final Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD;

    public RestoreGameCommand(UUID gameUUID, ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> playersField) {
        this.GAME_UUID = gameUUID;
        this.GAME_DTO = gameDTO;
        this.CURRENT_STATE = currentState;
        this.PLAYERS_FIELD = playersField;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.restoreGame(GAME_UUID, GAME_DTO, CURRENT_STATE, PLAYERS_FIELD);
    }
}
