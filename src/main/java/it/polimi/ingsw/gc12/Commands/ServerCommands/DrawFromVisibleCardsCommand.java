package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command to draw a card from the visible cards of a specified deck.
 * Implements the {@link ServerCommand} interface.
 */
public class DrawFromVisibleCardsCommand implements ServerCommand {

    private final String DECK;
    private final int POSITION;

    /**
     * Constructs a DrawFromVisibleCardsCommand to draw a card from the visible cards of the specified deck.
     *
     * @param deck     The name or identifier of the deck from which to draw the card.
     * @param position The position of the card in the visible cards list to draw from.
     */
    public DrawFromVisibleCardsCommand(String deck, int position) {
        this.DECK = deck;
        this.POSITION = position;
    }

    /**
     * Executes the command by requesting to the server controller to draw a card from the visible cards of the specified deck.
     *
     * @param caller           The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.drawFromVisibleCards(caller, DECK, POSITION);
    }

}
