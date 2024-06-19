package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class PickObjectiveCommand implements ServerCommand {

    private final int CARD_ID;

    public PickObjectiveCommand(int cardId) {
        this.CARD_ID = cardId;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.pickObjective(caller, CARD_ID);
    }
}
