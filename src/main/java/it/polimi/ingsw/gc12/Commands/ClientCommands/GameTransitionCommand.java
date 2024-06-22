package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to transition the game state to the next phase or round.
 * Implements the {@link ClientCommand} interface.
 */
public class GameTransitionCommand implements ClientCommand{

    /** The current round number in the game. */
    private final int ROUND;
    /**
     * Index of the current player in the game's list of players.
     * Values [0, MAX_PLAYERS): Index of a specific player based on the number of players in a game.
     * Value -1 is used as a placeholder indicating that the following game phase has to be played by everybody.
     */
    private final int CURRENT_PLAYER_INDEX;
    /**
     * Number of turns (Play phase + Draw phase) until the game ends.
     * Values [0, 2 * MAX_PLAYERS): Number of turns until the game ends.
     * Value -1 indicates that the game is not in the final phase.
     */
    private final int TURNS_LEFT_UNTIL_GAME_ENDS;

    /**
     * Constructs a GameTransitionCommand with the specified parameters.
     *
     * @param round                    The current round number in the game.
     * @param currentPlayerIndex       Index of the current player in the game's list of players.
     * @param turnsLeftUntilGameEnds   Number of turns (Play phase + Draw phase) until the game ends.
     */
    public GameTransitionCommand(int round, int currentPlayerIndex, int turnsLeftUntilGameEnds) {
        this.ROUND = round;
        this.CURRENT_PLAYER_INDEX = currentPlayerIndex;
        this.TURNS_LEFT_UNTIL_GAME_ENDS = turnsLeftUntilGameEnds;
    }

    /**
     * Executes the command on the provided client controller, transitioning the game state to the next phase or round.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    public void execute(ClientControllerInterface clientController){
        clientController.transition(ROUND, CURRENT_PLAYER_INDEX, TURNS_LEFT_UNTIL_GAME_ENDS);
    }
}
