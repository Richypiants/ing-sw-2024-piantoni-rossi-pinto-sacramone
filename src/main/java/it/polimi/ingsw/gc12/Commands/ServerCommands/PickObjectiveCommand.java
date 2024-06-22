package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command for a client to pick an objective card.
 * Implements the {@link ServerCommand} interface.
 */
public class PickObjectiveCommand implements ServerCommand {

    private final int CARD_ID;

    /**
     * Constructs a PickObjectiveCommand with the specified objective card ID.
     *
     * @param cardId The ID of the objective card chosen by the client.
     */
    public PickObjectiveCommand(int cardId) {
        this.CARD_ID = cardId;
    }

    /**
     * Executes the command by requesting to the server controller to handle the client's objective card selection.
     *
     * @param caller           The network session of the client initiating the command.
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.pickObjective(caller, CARD_ID);
    }
}
