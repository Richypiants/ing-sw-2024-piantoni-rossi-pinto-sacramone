package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

/**
 * Represents a client command to replace various types of cards in the game.
 * Implements the {@link ClientCommand} interface.
 */
public class ReplaceCardsCommand implements ClientCommand {

    private final List<Triplet<Integer, String, Integer>> CARD_PLACEMENTS;

    /**
     * Constructs a ReplaceCardsCommand with the specified list of card placements.
     *
     * @param cardPlacements The list of triplets containing card ID, placement type, and placement index.
     */
    public ReplaceCardsCommand(List<Triplet<Integer, String, Integer>> cardPlacements) {
        this.CARD_PLACEMENTS = cardPlacements;
    }

    /**
     * Executes the command on the provided client controller, containing the instructions to perform the replacement of cards based on the received infos.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.replaceCard(CARD_PLACEMENTS);
    }
}
