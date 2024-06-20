package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

import java.util.List;

public class ReceiveObjectiveChoiceCommand implements ClientCommand {

    private final List<Integer> CARD_IDS;

    public ReceiveObjectiveChoiceCommand(List<Integer> cardIDs) {
        this.CARD_IDS = cardIDs;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.receiveObjectiveChoice(CARD_IDS);
    }
}
