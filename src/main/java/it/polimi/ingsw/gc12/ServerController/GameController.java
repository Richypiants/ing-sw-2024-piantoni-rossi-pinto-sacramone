package it.polimi.ingsw.gc12.ServerController;

import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;

public class GameController {

    public GameController() {

    }

    public Game startGame(GameLobby lobby) {

        return null;
    }

    private void transition(Game toExecute) { //FIXME: nome orripilante...
        toExecute.getCurrentState().transition();
    }
}
