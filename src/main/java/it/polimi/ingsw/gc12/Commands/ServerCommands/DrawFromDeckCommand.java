package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command to draw a card from a specified deck.
 * Implements the {@link ServerCommand} interface.
 */
public class DrawFromDeckCommand implements ServerCommand {

    private final String DECK;

    /**
     * Constructs a DrawFromDeckCommand to draw a card from the specified deck.
     *
     * @param deck The name or identifier of the deck from which the card will be drawn.
     */
    public DrawFromDeckCommand(String deck) {
        DECK = deck;
    }

    /**
     * Executes the command by requesting to the server controller to draw a card from the specified deck.
     *
     * @param caller           The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.drawFromDeck(caller, DECK);
    }
}

