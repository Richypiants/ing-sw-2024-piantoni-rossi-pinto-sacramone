package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestoreGameCommand implements ClientCommand {

    private final ClientGame GAME_DTO;
    private final String CURRENT_STATE;
    private final Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD;

    public RestoreGameCommand(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> playersField) {
        this.GAME_DTO = gameDTO;
        this.CURRENT_STATE = currentState;
        this.PLAYERS_FIELD = playersField;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.restoreGame(GAME_DTO, CURRENT_STATE, PLAYERS_FIELD);
    }
}
