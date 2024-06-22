package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

/**
 * Represents the state of the game client where the player needs to on which side to place the initial card on the game board.
 * Extends {@link GameScreenState}.
 */
public class ChooseInitialCardsState extends GameScreenState {

    /**
     * Constructs a ChooseInitialCardsState and initializes the TUI commands specific to this state.
     */
    public ChooseInitialCardsState() {
        TUICommands = List.of(
                "'[pickInitial | pi] <side> [front | back]' to place your initial card",
                "'[broadcastMessage | bm] <message>' to send a message to all players (max 200 chars)",
                "'[directMessage | dm] <recipient> <message>' to send a private message (max 200 chars)",
                "Remember that you can always type 'quit' and then reconnect to this game"
        );
    }

    /**
     * Executes the behavior of the choose initial cards state by displaying the game screen and showing the initial cards side choice if necessary.
     */
    @Override
    public void executeState() {
        selectedView.gameScreen();
        if (!ClientController.getInstance().VIEWMODEL.getCurrentGame().getThisPlayer().getPlacedCards().containsKey(new GenericPair<>(0, 0)))
            selectedView.showInitialCardsChoice();
    }

    /**
     * Restores the screen state by displaying the game screen and showing the initial cards side choice if necessary.
     */
    @Override
    public void restoreScreenState() {
        selectedView.gameScreen();
        if (!CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getThisPlayer().getPlacedCards().containsKey(new GenericPair<>(0, 0)))
            selectedView.showInitialCardsChoice();
    }

    /**
     * Shows the placed card if the given nickname matches the player's own nickname.
     *
     * @param nickname The nickname of the player who placed the card.
     */
    @Override
    public void showPlacedCard(String nickname) {
        if (nickname.equals(CLIENT_CONTROLLER.VIEWMODEL.getOwnNickname()))
            selectedView.gameScreen();
    }

    /**
     * Places a card at the specified coordinates with the given side by sending the card placement request to the server.
     *
     * @param coordinates    The coordinates where the card is to be placed.
     * @param inHandPosition The position of the card in hand (1-based index).
     * @param playedSide     The side of the card to be played.
     */
    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition - 1, playedSide);
    }

    /**
     * Handles the transition to another state, currently empty as no specific transition logic is needed for this state.
     */
    @Override
    public void transition() {
    }

    /**
     * Returns a string representation of the choose initial cards state.
     *
     * @return The string "Initial Card side choice phase".
     */
    @Override
    public String toString() {
        return "Initial Card side choice phase";
    }
}

