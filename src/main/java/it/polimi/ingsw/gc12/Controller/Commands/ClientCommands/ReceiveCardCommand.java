package it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class ReceiveCardCommand implements ClientCommand {

    private final int CARD_ID;

    public ReceiveCardCommand(int cardID) {
        this.CARD_ID = cardID;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.receiveCard(CARD_ID);
    }
}
