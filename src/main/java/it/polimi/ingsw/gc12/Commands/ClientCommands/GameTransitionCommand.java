package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class GameTransitionCommand implements ClientCommand{

    private final int ROUND;
    /**
     * Values [0,MAX_PLAYERS): index of a specific player based on the number of players in a game
     * Value -1 used as a placeholder indicating that the following gamePhase has to be played by everybody;
     */
    private final int CURRENT_PLAYER_INDEX;

    public GameTransitionCommand(int round, int currentPlayerIndex){
        this.ROUND = round;
        this.CURRENT_PLAYER_INDEX = currentPlayerIndex;
    }

    public void execute(ClientControllerInterface clientController){
        clientController.transition(ROUND, CURRENT_PLAYER_INDEX);
    }

}
