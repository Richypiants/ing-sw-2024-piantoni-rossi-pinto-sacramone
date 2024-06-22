package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

import java.util.List;

/**
 * Represents a client command to receive objective card choices.
 * Implements the {@link ClientCommand} interface.
 */
public class ReceiveObjectiveChoiceCommand implements ClientCommand {

    private final List<Integer> CARD_IDS;

    /**
     * Constructs a ReceiveObjectiveChoiceCommand with the specified list of card IDs.
     *
     * @param cardIDs The list of card IDs representing the objective card choices received.
     */
    public ReceiveObjectiveChoiceCommand(List<Integer> cardIDs) {
        this.CARD_IDS = cardIDs;
    }

    /**
     * Executes the command on the provided client controller, containing the received objective card choices.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.receiveObjectiveChoice(CARD_IDS);
    }
}
