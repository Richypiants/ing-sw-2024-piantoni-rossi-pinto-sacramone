package it.polimi.ingsw.gc12.View.Client.TUI.TUIViews;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.View.Client.TUI.TUIParser;
import it.polimi.ingsw.gc12.View.Client.View;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Singleton class representing the Terminal User Interface (TUI) view.
 * It extends the abstract View class and implements various methods for displaying
 * different screens and handling user input in the terminal.
 */
public class TUIView extends View {

    /**
     * Console instance for reading user input.
     */
    public static final Console console = System.console();
    /**
     * Instance of TUIParser for parsing user input commands.
     */
    public static TUIParser listener;
    /**
     * Singleton instance of TUIView.
     */
    private static TUIView SINGLETON_TUI_INSTANCE = null;
    /**
     * Terminal size configuration (rows x columns).
     */
    private final GenericPair<Integer, Integer> TERMINAL_SIZE = new GenericPair<>(49, 211); //x: rows, y:columns

    /**
     * Private constructor to enforce Singleton pattern.
     */
    public TUIView() {}

    /**
     * Returns the single instance of TUIView, creating it if necessary.
     *
     * @return The singleton instance of TUIView.
     */
    public static TUIView getInstance() {
        if (SINGLETON_TUI_INSTANCE == null) {
            SINGLETON_TUI_INSTANCE = new TUIView();

            AnsiConsole.systemInstall();
            listener = TUIParser.getInstance();

            try {
                new ProcessBuilder("cmd", "/c", "mode con:cols=" + SINGLETON_TUI_INSTANCE.TERMINAL_SIZE.getY() + " lines=" + SINGLETON_TUI_INSTANCE.TERMINAL_SIZE.getX())
                        .inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                try {
                    //If running on MacOSX or Linux, run on bash instead of cmd
                    new ProcessBuilder("bash", "/c", "mode con:cols=" + SINGLETON_TUI_INSTANCE.TERMINAL_SIZE.getY() + " lines=" + SINGLETON_TUI_INSTANCE.TERMINAL_SIZE.getX())
                            .inheritIO().start().waitFor();
                } catch (InterruptedException | IOException e2) {
                    throw new RuntimeException(e2); //Should never happen for the 3 main OSs
                }
            }
        }
        return SINGLETON_TUI_INSTANCE;
    }


    /**
     * Clears the terminal screen and positions the cursor for new input.
     */
    public static void clearTerminal() {
        System.out.print(ansi()
                .saveCursorPosition()
                .cursor(TUIParser.COMMAND_INPUT_ROW - 2, 1)
                .eraseScreen(Ansi.Erase.BACKWARD).eraseLine(Ansi.Erase.FORWARD)
                .cursorDownLine()
                .a("------------------------------------------------------------------").eraseLine(Ansi.Erase.FORWARD)
                .cursorDownLine()
                .a("> [" + ClientController.getInstance().VIEWMODEL.getOwnNickname() + "] ")
                .restoreCursorPosition()
                .eraseScreen(Ansi.Erase.FORWARD)
        );
    }

    /**
     * Prints an error message at the designated position in the terminal.
     *
     * @param error The throwable containing the error message.
     */
    @Override
    public void printError(Throwable error) {
        System.out.print(ansi().saveCursorPosition()
                .cursor(TUIParser.EXCEPTIONS_ROW, 1)
                .a(error.getMessage()).reset()
                .restoreCursorPosition()
        );
    }

    /**
     * Prints the provided ANSI formatted content at the current cursor position.
     *
     * @param toPrint The ANSI content to print.
     */
    public void printToPosition(Ansi toPrint) {
        System.out.print(ansi().saveCursorPosition()
                .a(toPrint).reset()
                .restoreCursorPosition()
                .eraseScreen(Ansi.Erase.FORWARD)
        );
    }

    /**
     * Displays the title screen.
     */
    @Override
    public void titleScreen() {
        TUITitleView.getInstance().titleScreen();
    }

    /**
     * Displays the connection setup screen.
     */
    @Override
    public void connectionSetupScreen() {
        TUIConnectionView.getInstance().connectionSetupScreen();
    }

    /**
     * Prompts the user to retry the connection if it failed.
     *
     * @param causedByNetworkError True if the connection failed due to a network error.
     * @return True if the user decides to retry the connection.
     */
    @Override
    public boolean retryConnectionPrompt(boolean causedByNetworkError) {
        return TUIConnectionView.getInstance().retryConnectionPrompt(causedByNetworkError);
    }

    /**
     * Displays a confirmation message for a successful connection.
     */
    @Override
    public void connectedConfirmation() {
        TUIConnectionView.getInstance().connectedConfirmation();
    }

    /**
     * Displays the disconnected screen and attempts to reconnect.
     */
    @Override
    public void disconnectedScreen() {
        TUIParser.COMMAND_INPUT_COLUMN = 6;
        TUIParser.isReading = false;
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseLine(Ansi.Erase.FORWARD));
        clearTerminal();
        printToPosition(ansi().cursor(1, 1).a("Connection to server lost: trying to reconnect..."));
    }

    /**
     * Displays the quitting screen.
     */
    @Override
    public void quittingScreen() {
        clearTerminal();
        printToPosition(ansi().cursor(1, 1).a("Returning to title screen..."));
    }

    /**
     * Displays the lobbies screen.
     */
    @Override
    public void lobbiesScreen(){
        TUILobbiesView.getInstance().lobbiesScreen();
    }

    /**
     * Displays the user's nickname.
     */
    @Override
    public void showNickname() {
        TUILobbiesView.getInstance().showNickname();
    }

    /**
     * Displays the game screen.
     */
    @Override
    public void gameScreen() {
        TUIGameView.getInstance().gameScreen();
    }

    /**
     * Displays the awaiting screen while waiting for other players.
     */
    @Override
    public void awaitingScreen() {
        TUIGameView.getInstance().awaitingScreen();
    }

    /**
     * Updates the chat screen.
     */
    @Override
    public void updateChat() {
        TUIGameView.getInstance().updateChat();
    }

    /**
     * Displays the initial card choice screen.
     */
    @Override
    public void showInitialCardsChoice(){
        TUIGameView.getInstance().showInitialCardsChoice();
    }

    /**
     * Displays the objective card choice screen.
     *
     * @param objectivesSelection List of objective cards to choose from.
     */
    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        TUIGameView.getInstance().showObjectiveCardsChoice(objectivesSelection);
    }

    /**
     * Displays the user's hand of cards.
     */
    @Override
    public void showHand() {
        TUIGameView.getInstance().showHand();
    }

    /**
     * Displays the common placed cards on the game board.
     */
    @Override
    public void showCommonPlacedCards(){
        TUIGameView.getInstance().showCommonPlacedCards();
    }

    /**
     * Displays the field of the specified player.
     *
     * @param player The player whose field is to be displayed.
     */
    @Override
    public void showField(ClientPlayer player) {
        TUIGameView.getInstance().showField(player);
    }

    /**
     * Moves the field currently displayed by x cards left and y cards down.
     *
     * @param centerOffset The offset by which to move the field view.
     */
    @Override
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        TUIGameView.getInstance().moveField(centerOffset);
    }

    /**
     * Displays the leaderboard screen with the given points statistics.
     *
     * @param leaderboard List of triplets containing player names, scores, and ranks.
     * @param gameEndedDueToDisconnections True if the game ended due to disconnections.
     */
    @Override
    public void leaderboardScreen(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
        TUIGameView.getInstance().leaderboardScreen(leaderboard, gameEndedDueToDisconnections);
    }
}
