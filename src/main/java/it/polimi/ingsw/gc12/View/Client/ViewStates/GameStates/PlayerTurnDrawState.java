package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.DrawFromDeckCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.DrawFromVisibleCardsCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

/**
 * Represents the state where a player is allowed to draw cards during their turn.
 * Extends {@link GameScreenState}.
 */
public class PlayerTurnDrawState extends GameScreenState {

    /**
     * Constructor initializing the available TUI commands based on whether it's this client's turn.
     */
    public PlayerTurnDrawState() {
        TUICommands = CLIENT_CONTROLLER.isThisClientTurn() ?
                List.of(
                        "'[drawFromDeck | dfd] <deck>' [resource | gold]",
                        "'[drawFromVisibleCards | dfvc] <deck> <position>' [resource | gold] [1 | 2]",
                        "'[showField | sf] <playerID>' to show the player's field",
                        "'[moveField | mf] <x> <y>' moves the field by x cards left and y cards down",
                        "'[broadcastMessage | bm] <message>' to send a message to all players (200 chars max.)",
                        "'[directMessage | dm] <recipient> <message>' to send a private message (200 chars max.)") :
                List.of(
                        "'[showField | sf] <playerID>' to show the player's field",
                        "'[moveField | mf] <x> <y>' moves the field by x cards left and y cards down",
                        "'[broadcastMessage | bm] <message>' to send a message to all players (200 chars max.)",
                        "'[directMessage | dm] <recipient> <message>' to send a private message (200 chars max.)");
    }

    /**
     * Executes the behavior specific to the current game screen state, setting the view to the game screen.
     */
    @Override
    public void executeState() {
        selectedView.gameScreen();
    }

    /**
     * Restores the screen state to the current game screen state.
     */
    public void restoreScreenState() {
        selectedView.gameScreen();
    }

    /**
     * Handles drawing a card from the deck.
     *
     * @param deck The deck from which to draw a card (either 'resource' or 'gold').
     */
    @Override
    public void drawFromDeck(String deck) {
        if (invalidDeck(deck)) {
            selectedView.printError(new IllegalArgumentException("The provided deck doesn't exist!"));
        } else {
            CLIENT.requestToServer(new DrawFromDeckCommand(deck));
        }
    }

    /**
     * Handles drawing a card from the visible cards.
     *
     * @param deck     The deck from which to draw a visible card (either 'resource' or 'gold').
     * @param position The position of the card in the visible cards area (1-based index).
     */
    @Override
    public void drawFromVisibleCards(String deck, int position) {
        if (invalidDeck(deck)) {
            selectedView.printError(new IllegalArgumentException("The provided visible card area doesn't exist!"));
        } else if (position != 1 && position != 2) {
            selectedView.printError(new IllegalArgumentException("The provided position doesn't exist!"));
        } else {
            CLIENT.requestToServer(new DrawFromVisibleCardsCommand(deck, position - 1));
        }
    }

    /**
     * Shows the field of a specified player.
     *
     * @param playerID The ID of the player whose field is to be shown.
     */
    public void showField(int playerID) {
        ClientGame game = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame();
        if (playerID < 0 || playerID > game.getPlayersNumber()) {
            selectedView.printError(new IllegalArgumentException("The provided ID doesn't match a player's ID in the game!"));
        } else {
            selectedView.showField(game.getPlayers().get(playerID - 1));
        }
    }

    /**
     * Moves the field view by a specified offset.
     *
     * @param centerOffset The offset by which to move the field view.
     */
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.moveField(centerOffset);
    }

    /**
     * Checks if the provided deck name is invalid.
     *
     * @param deck The deck name to check.
     * @return true if the deck name is invalid, false otherwise.
     */
    private boolean invalidDeck(String deck) {
        return !(deck.equalsIgnoreCase("resource") || deck.equalsIgnoreCase("gold"));
    }

    /**
     * Transitions to the next game state, which is the player turn play state.
     */
    @Override
    public void transition() {
        currentState = new PlayerTurnPlayState();
    }

    /**
     * Returns a string representation of this state.
     *
     * @return A string indicating the current state.
     */
    @Override
    public String toString() {
        return "draw phase";
    }
}
