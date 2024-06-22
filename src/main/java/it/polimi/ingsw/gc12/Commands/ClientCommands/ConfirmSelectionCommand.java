package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to confirm the selection of an objective card.
 * Implements the {@link ClientCommand} interface.
 */
public class ConfirmSelectionCommand implements ClientCommand{

    private final int SELECTED_CARD_ID;

    /**
     * Constructs a ConfirmSelectionCommand with the specified selected card ID.
     *
     * @param cardID The ID of the objective card that has been selected.
     */
    public ConfirmSelectionCommand(int cardID) {
        this.SELECTED_CARD_ID = cardID;
    }

    /**
     * Executes the command on the provided client controller, confirming the selection
     * of the objective card with the stored card ID.
     *
     * @param controller The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface controller) {
        controller.confirmObjectiveChoice(SELECTED_CARD_ID);
    }

}
