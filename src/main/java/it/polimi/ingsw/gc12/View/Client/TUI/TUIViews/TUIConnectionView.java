package it.polimi.ingsw.gc12.View.Client.TUI.TUIViews;

import it.polimi.ingsw.gc12.View.Client.TUI.TUIParser;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Singleton class representing the connection setup view in the Terminal User Interface (TUI).
 * It extends the TUIView class and implements methods for handling connection setup and retry prompts.
 */
public class TUIConnectionView extends TUIView {

    /**
     * Singleton instance of TUIConnectionView.
     */
    private static TUIConnectionView connectionView = null;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private TUIConnectionView() {
        super();
    }

    /**
     * Returns the single instance of TUIConnectionView, creating it if necessary.
     *
     * @return The singleton instance of TUIConnectionView.
     */
    public static TUIConnectionView getInstance() {
        if (connectionView == null) {
            connectionView = new TUIConnectionView();
        }
        return connectionView;
    }

    /**
     * Reads input from the console until a valid input from the provided list is entered.
     *
     * @param prompt     The ANSI-formatted prompt to display before reading input.
     * @param validInput The list of valid input options.
     * @return The selected input string from the valid options.
     */
    private String readUntil(Ansi prompt, List<String> validInput) {
        String selection = null;
        try {
            do {
                clearTerminal();
                System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN)
                        .eraseLine(Ansi.Erase.FORWARD)
                );
                printToPosition(ansi().a(prompt));
                selection = console.readLine();
                selection = selection.trim().toLowerCase();
            } while (!validInput.contains(selection));
        } catch (Exception e) {
            System.exit(-1);
        }

        return selection;
    }

    /**
     * Displays the connection setup screen, allowing the user to input server IP address, communication technology,
     * and nickname for connecting to the server.
     */
    @Override
    public void connectionSetupScreen() {
        clearTerminal();
        printToPosition(ansi().cursor(1, 1).a("Enter the server IP address (leave empty for 'localhost'): "));
        String serverIPAddress = console.readLine();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));

        clearTerminal();
        String communicationTechnology = readUntil(
                ansi().cursor(1, 1).a("Choose the communication technology (RMI-Socket): "),
                List.of("rmi", "socket")
        );
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));

        clearTerminal();
        String nickname;
        boolean lastInputWasInvalid = false;
        final int MAX_NICK_LENGTH = 10;

        do {
            printToPosition(ansi().cursor(1, 1).a("Enter your nickname [max " + MAX_NICK_LENGTH + " chars]: "));
            if(lastInputWasInvalid)
                printToPosition(ansi().cursor(2, 1).a("The entered nickname is longer than " + MAX_NICK_LENGTH + " characters or is empty! Retry..."));
            lastInputWasInvalid = false;
            nickname = console.readLine().trim();
            if(nickname.length() > MAX_NICK_LENGTH || nickname.isEmpty())
                lastInputWasInvalid = true;
            clearTerminal();
            System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN)
                    .eraseLine(Ansi.Erase.FORWARD)
                    .cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN));
        }while(lastInputWasInvalid);

        printToPosition(ansi().cursor(1, 1).a("Connecting to the server..."));

        ViewState.getCurrentState().connect(serverIPAddress, communicationTechnology, nickname);
    }

    /**
     * Displays a prompt asking the user if they want to retry the connection due to a network error or nickname conflict.
     *
     * @param causedByNetworkError True if the retry prompt is due to a network error, false if due to nickname conflict.
     * @return True if the user chooses to retry, false otherwise.
     */
    @Override
    public boolean retryConnectionPrompt(boolean causedByNetworkError) {
        clearTerminal();

        String promptText = "It seems " + (
                causedByNetworkError ?
                        "a network error occurred" :
                        "your chosen nickname is already in use"
        ) + ": would you like to retry? (Yes-No):";

        String wantsToRetry = readUntil(ansi().cursor(1, 1).a(promptText), List.of("yes", "no"));
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));

        if (wantsToRetry.equals("no")) {
            quittingScreen();
            System.exit(0);
        } else {
            clearTerminal();
            printToPosition(ansi().cursor(1, 1).a("Connecting to the server..."));
        }

        return wantsToRetry.equals("yes");
    }

    /**
     * Displays a confirmation message upon successful connection to the server.
     */
    @Override
    public void connectedConfirmation() {
        printToPosition(ansi().cursor(3, 1).a("Successfully connected to the server: nickname confirmed!"));
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e); //Should never happen
        }

        TUIParser.COMMAND_INPUT_COLUMN = 6 + VIEWMODEL.getOwnNickname().length();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));

        listener.startReading();
    }

}
