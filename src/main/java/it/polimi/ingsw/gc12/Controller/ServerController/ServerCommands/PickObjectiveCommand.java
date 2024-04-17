package it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class PickObjectiveCommand implements ServerCommand {

    private final int CARD_ID;

    public PickObjectiveCommand(int cardId) {
        this.CARD_ID = cardId;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) throws Exception {
        serverController.pickObjective(caller, CARD_ID);
    }
}
