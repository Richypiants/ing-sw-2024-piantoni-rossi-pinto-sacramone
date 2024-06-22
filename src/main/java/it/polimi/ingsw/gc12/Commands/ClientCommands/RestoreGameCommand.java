package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a client command to restore a game state on the client side.
 * Implements the {@link ClientCommand} interface.
 */
public class RestoreGameCommand implements ClientCommand {

    private final ClientGame GAME_DTO;
    private final String CURRENT_STATE;
    private final Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD;

    /**
     * Constructs a RestoreGameCommand with the specified game DTO, current state, and player's field state.
     *
     * @param gameDTO       The client game DTO representing the restored game state.
     * @param currentState  The current state identifier indicating where the game was paused.
     * @param playersField  The mapping of players' field states containing card placements and associated information.
     */
    public RestoreGameCommand(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> playersField) {
        this.GAME_DTO = gameDTO;
        this.CURRENT_STATE = currentState;
        this.PLAYERS_FIELD = playersField;
    }

    /**
     * Executes the command on the provided client controller, requesting to restore the game state using the stored DTO,
     * current state, and players' field information.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.restoreGame(GAME_DTO, CURRENT_STATE, PLAYERS_FIELD);
    }
}
