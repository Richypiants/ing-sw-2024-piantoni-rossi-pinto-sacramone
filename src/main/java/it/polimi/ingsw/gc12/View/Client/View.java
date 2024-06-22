package it.polimi.ingsw.gc12.View.Client;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.ClientModel.ViewModel;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.View.Client.GUI.GUIViews.GUIView;
import it.polimi.ingsw.gc12.View.Client.TUI.TUIViews.TUIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing the view of a client-side application.
 * It defines methods for displaying different screens and handling user interactions.
 * Existing view implementations include:
 *  * <ul>
 *  *     <li>{@link TUIView}</li>
 *  *     <li>{@link GUIView}</li>
 *  * </ul>
 */
public abstract class View {

    /**
     * Singleton instance of the client controller.
     */
    protected final static ClientController CLIENT_CONTROLLER = ClientController.getInstance();

    /**
     * Singleton instance of the view model managed by the client controller.
     */
    protected final static ViewModel VIEWMODEL = CLIENT_CONTROLLER.VIEWMODEL;

    /**
     * Displays an error message on the view.
     *
     * @param error The error object or exception to be displayed.
     */
    public abstract void printError(Throwable error);

    /**
     * Displays the title screen of the application.
     */
    public abstract void titleScreen();

    /**
     * Displays the connection setup screen.
     */
    public abstract void connectionSetupScreen();

    /**
     * Prompts the user for retrying the connection.
     *
     * @param causedByNetworkError Indicates if the retry prompt is caused by a network error.
     * @return True if the user wants to retry the connection; false otherwise.
     */
    public abstract boolean retryConnectionPrompt(boolean causedByNetworkError);

    /**
     * Displays confirmation upon successful connection.
     */
    public abstract void connectedConfirmation();

    /**
     * Displays the disconnected screen upon losing connection.
     */
    public abstract void disconnectedScreen();

    /**
     * Displays the quitting screen when the user decides to quit.
     */
    public abstract void quittingScreen();

    /**
     * Displays the lobbies screen showing available game lobbies.
     */
    public abstract void lobbiesScreen();

    /**
     * Displays the game screen during active gameplay.
     */
    public abstract void gameScreen();

    /**
     * Displays the awaiting screen while waiting for other players or game events.
     */
    public abstract void awaitingScreen();

    /**
     * Displays the nickname of the client player.
     */
    public abstract void showNickname();

    /**
     * Updates the chat display with new messages.
     */
    public abstract void updateChat();

    /**
     * Displays the initial card choice screen for the client player.
     */
    public abstract void showInitialCardsChoice();

    /**
     * Displays the objective card choice screen for the client player.
     *
     * @param objectivesSelection The list of objective cards available for selection.
     */
    public abstract void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection);

    /**
     * Displays the common placed cards on the board.
     */
    public abstract void showCommonPlacedCards();

    /**
     * Displays the leaderboard screen showing points statistics.
     *
     * @param POINTS_STATS                  List of player points statistics.
     * @param gameEndedDueToDisconnections  Indicates if the game ended due to disconnections.
     */
    public abstract void leaderboardScreen(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections);

    /**
     * Displays the game field of the client player.
     *
     * @param player The client player whose field is to be displayed.
     */
    public abstract void showField(ClientPlayer player);

    /**
     * Moves the game field display with a specified center offset.
     *
     * @param centerOffset The offset to move the center of the field display.
     */
    public abstract void moveField(GenericPair<Integer, Integer> centerOffset);

    /**
     * Displays the hand of cards held by the client player.
     */
    public abstract void showHand();
}
