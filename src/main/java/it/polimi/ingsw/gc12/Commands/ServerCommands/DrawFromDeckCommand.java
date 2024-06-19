package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class DrawFromDeckCommand implements ServerCommand {

    private final String DECK;

    public DrawFromDeckCommand(String deck) {
        DECK = deck;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.drawFromDeck(caller, DECK);
    }
}
