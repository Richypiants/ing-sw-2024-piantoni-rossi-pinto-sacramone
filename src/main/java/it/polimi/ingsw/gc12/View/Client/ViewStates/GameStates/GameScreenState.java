package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.BroadcastMessageCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.DirectMessageCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.LeaveGameCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

/**
 * Represents an abstract game screen state, providing common functionalities for different game states.
 * Extends {@link ViewState}.
 */
public abstract class GameScreenState extends ViewState {

    /**
     * Executes the behavior specific to the current game screen state.
     */
    @Override
    public abstract void executeState();

    /**
     * Restores the screen state to the current game screen state.
     */
    public abstract void restoreScreenState();

    /**
     * Sends a card placement command to the server with the specified coordinates, hand position, and side.
     *
     * @param coordinates   The coordinates where the card should be placed.
     * @param inHandPosition The position of the card in the player's hand (0-based index).
     * @param playedSide    The side of the card to be played (front or back).
     */
    protected void sendCardToPlace(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        ClientCard card;
        try {
            card = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getCardsInHand().get(inHandPosition);
        } catch (IndexOutOfBoundsException e) {
            selectedView.printError(new IllegalArgumentException("There's no card in the specified hand position!"));
            return;
        }

        CLIENT.requestToServer(new PlaceCardCommand(coordinates, card.ID, playedSide));
    }

    /**
     * Sends a broadcast message command to the server.
     *
     * @param message The message to be broadcast to all players.
     */
    @Override
    public void broadcastMessage(String message) {
        CLIENT.requestToServer(new BroadcastMessageCommand(message));
    }

    /**
     * Sends a direct message command to the server.
     *
     * @param receiverNickname The nickname of the player to receive the message.
     * @param message          The message to be sent.
     */
    @Override
    public void directMessage(String receiverNickname, String message) {
        CLIENT.requestToServer(new DirectMessageCommand(receiverNickname, message));
    }

    /**
     * Displays a received chat message, updating the chat log.
     *
     * @param message The received message to be displayed.
     */
    @Override
    public void showReceivedChatMessage(String message) {
        if (message.length() < 90) {
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message);
        } else {
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message.substring(0, 90));
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message.substring(90));
        }
        selectedView.updateChat();
    }

    /**
     * Handles quitting the game, sending a leave game command to the server and displaying the quitting screen.
     */
    @Override
    public void quit() {
        new Thread(() -> {
            synchronized (CLIENT) {
                try {
                    CLIENT.requestToServer(new LeaveGameCommand());
                    selectedView.quittingScreen();
                    // Notified by CLIENT.requestToServer() function, which gets executed in another thread
                    CLIENT.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); // Should never happen
                }
            }
            super.quit();
        }).start();
    }

    /**
     * Handles the transition to the next game state.
     */
    public abstract void transition();
}
