package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public class ReplaceCardsCommand implements ClientCommand {

    private final List<Triplet<Integer, String, Integer>> CARD_PLACEMENTS;

    public ReplaceCardsCommand(List<Triplet<Integer, String, Integer>> cardPlacements) {
        this.CARD_PLACEMENTS = cardPlacements;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.replaceCard(CARD_PLACEMENTS);
    }
}
