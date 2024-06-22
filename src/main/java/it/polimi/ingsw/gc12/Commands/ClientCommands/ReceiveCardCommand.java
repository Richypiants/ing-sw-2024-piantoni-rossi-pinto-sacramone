package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;

/**
 * Represents a client command to receive a card.
 * Implements the {@link ClientCommand} interface.
 */
public class ReceiveCardCommand implements ClientCommand {

    private final int CARD_ID;

    /**
     * Constructs a ReceiveCardCommand with the specified card ID.
     *
     * @param cardID The ID of the card received.
     */
    public ReceiveCardCommand(int cardID) {
        this.CARD_ID = cardID;
    }

    /**
     * Executes the command on the provided client controller, requesting to add the received card.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.receiveCard(CARD_ID);
    }
}
