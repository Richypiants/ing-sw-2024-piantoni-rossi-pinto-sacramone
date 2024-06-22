package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

/**
 * Represents the state where a player is playing their turn by placing cards on the field,
 * moving the field view, or interacting with other players through messages.
 * Extends {@link GameScreenState}.
 */
public class PlayerTurnPlayState extends GameScreenState {

    /**
     * Constructor initializing the available TUI commands based on whether it's this client's turn.
     */
    public PlayerTurnPlayState() {
        TUICommands =
                CLIENT_CONTROLLER.isThisClientTurn() ?
                        List.of(
                                "'[placeCard | pc] <x> <y> [1 | 2 | 3] [front | back]' to place a card ",
                                "    on the field (<x>: x-coordinate, <y>: y-coordinate>)",
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
     * Places a card on the field.
     *
     * @param coordinates The coordinates where the card is to be placed.
     * @param inHandPosition The position of the card in hand.
     * @param playedSide The side of the card to be played.
     */
    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition - 1, playedSide);
    }

    /**
     * Shows the field of a specified player.
     *
     * @param playerID The ID of the player whose field is to be shown.
     */
    public void showField(int playerID) {
        ClientGame game = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame();
        if (playerID < 0 || playerID > game.getPlayersNumber()) {
            selectedView.printError(new IllegalArgumentException("The provided ID doesn't match to a player's ID in the game!"));
            return;
        }

        selectedView.showField(game.getPlayers().get(playerID - 1));
    }

    /**
     * Moves the field currently displayed by x cards left and y cards down.
     *
     * @param centerOffset The offset by which to move the field view.
     */
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.moveField(centerOffset);
    }

    /**
     * Transitions to the next game state, which is the player turn draw state.
     */
    @Override
    public void transition() {
        currentState = new PlayerTurnDrawState();
    }

    /**
     * Returns a string representation of this state.
     *
     * @return A string indicating the current state.
     */
    @Override
    public String toString() {
        return "play phase";
    }
}
