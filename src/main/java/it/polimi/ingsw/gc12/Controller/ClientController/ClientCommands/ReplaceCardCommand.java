package it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public class ReplaceCardCommand implements ClientCommand {

    private final List<Triplet<Integer, String, Integer>> CARD_PLACEMENTS;

    public ReplaceCardCommand(List<Triplet<Integer, String, Integer>> cardPlacements) {
        this.CARD_PLACEMENTS = cardPlacements;
    }

    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.replaceCard(CARD_PLACEMENTS);
    }
}
