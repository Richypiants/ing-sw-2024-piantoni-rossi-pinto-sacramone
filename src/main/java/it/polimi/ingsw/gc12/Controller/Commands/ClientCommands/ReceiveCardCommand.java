package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

import java.util.List;

public class ReceiveCardCommand implements ClientCommand {

    private final List<Integer> CARD_IDS;

    public ReceiveCardCommand(List<Integer> cardIDs) {
        this.CARD_IDS = cardIDs;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.receiveCard(CARD_IDS);
    }
}
