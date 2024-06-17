package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIParser;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.ansi;

public class TUIConnectionView extends TUIView {

    private static TUIConnectionView connectionView = null;

    private TUIConnectionView() {
        super();
    }

    public static TUIConnectionView getInstance() {
        if (connectionView == null) {
            connectionView = new TUIConnectionView();
        }
        return connectionView;
    }

    private String readUntil(Ansi prompt, List<String> validInput) {
        String selection;
        do {
            clearTerminal();
            System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN)
                    .eraseLine(Ansi.Erase.FORWARD)
            );
            printToPosition(ansi().a(prompt));
            selection = console.readLine().trim().toLowerCase();
        } while (!validInput.contains(selection));

        return selection;
    }

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
        String nickname = "";
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

    @Override
    public void connectedConfirmation() {
        printToPosition(ansi().cursor(3, 1).a("Successfully connected to the server: nickname confirmed!"));
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            CLIENT_CONTROLLER.ERROR_LOGGER.log(e);
        }

        TUIParser.COMMAND_INPUT_COLUMN = 6 + VIEWMODEL.getOwnNickname().length();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));
        listener.startReading();
    }

}
