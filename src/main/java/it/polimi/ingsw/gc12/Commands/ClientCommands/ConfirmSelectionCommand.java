package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

public class ConfirmSelectionCommand implements ClientCommand{

    private final int SELECTED_CARD_ID;

    public ConfirmSelectionCommand(int cardID) {
        this.SELECTED_CARD_ID = cardID;
    }

    @Override
    public void execute(ClientControllerInterface controller) {
        controller.confirmObjectiveChoice(SELECTED_CARD_ID);
    }

}
