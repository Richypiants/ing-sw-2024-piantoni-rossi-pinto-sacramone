package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

import java.util.List;

public class ReceiveObjectiveChoice implements ClientCommand {

    private final List<Integer> CARD_IDS;

    public ReceiveObjectiveChoice(List<Integer> CARD_IDS){
        this.CARD_IDS = CARD_IDS;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.receiveObjectiveChoice(CARD_IDS);
    }
}
