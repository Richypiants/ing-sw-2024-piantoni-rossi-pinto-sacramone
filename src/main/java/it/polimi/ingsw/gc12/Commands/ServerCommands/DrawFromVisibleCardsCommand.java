package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

public class DrawFromVisibleCardsCommand implements ServerCommand {

    private final String DECK;
    private final int POSITION;

    public DrawFromVisibleCardsCommand(String deck, int position) {
        this.DECK = deck;
        this.POSITION = position;
    }

    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.drawFromVisibleCards(caller, DECK, POSITION);
    }

}
