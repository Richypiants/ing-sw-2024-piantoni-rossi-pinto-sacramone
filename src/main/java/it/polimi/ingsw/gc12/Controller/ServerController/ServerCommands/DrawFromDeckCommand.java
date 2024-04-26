package it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class DrawFromDeckCommand implements ServerCommand {

    private final String DECK;

    public DrawFromDeckCommand(String deck) {
        DECK = deck;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) {
        serverController.drawFromDeck(caller, DECK);
    }
}
