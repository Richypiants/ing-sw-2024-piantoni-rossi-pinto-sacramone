package it.polimi.ingsw.gc12.View.Client.TUI;

import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import java.io.Console;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Singleton class responsible for parsing and executing commands input through the Terminal User Interface (TUI).
 */
public class TUIParser {

    /**
     * The row of the terminal where commands submitted by the client are written.
     */
    public static int COMMAND_INPUT_ROW = 48;
    /**
     * The column of the terminal where commands submitted by the client are written.
     */
    public static int COMMAND_INPUT_COLUMN = 6;
    /**
     * The row of the terminal where exceptions are printed.
     */
    public static int EXCEPTIONS_ROW = 49;
    /**
     * A boolean value which is true if the TUIParser is currently reading from the console.
     */
    public static boolean isReading = false;
    /**
     * The singleton instance of the {@code TUIParser}.
     */
    private static TUIParser SINGLETON_TUI_LISTENER;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private TUIParser() {}

    /**
     * Returns the single instance of TUIParser, creating it if necessary.
     *
     * @return The singleton instance of TUIParser.
     */
    public static TUIParser getInstance() {
        synchronized (TUIParser.class) {
            if (SINGLETON_TUI_LISTENER == null)
                SINGLETON_TUI_LISTENER = new TUIParser();
            return SINGLETON_TUI_LISTENER;
        }
    }

    /**
     * Starts reading commands from the console in a separate thread.
     */
    public void startReading() {
        Console console = System.console();
        isReading = true;
        new Thread(() -> {
            String command = "";
            do {
                try {
                    command = console.readLine();
                } catch (NoSuchElementException ignored) {
                    System.exit(-1); //Should never be reached
                }
                System.out.print(ansi().cursor(COMMAND_INPUT_ROW, COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));
                parseCommand(command);
            } while (isReading && command != null && !command.equalsIgnoreCase("quit"));
        }).start();
    }

    /**
     * Parses and executes the command from the given list of tokens.
     *
     * @param tokens The list of tokens composing the command.
     */
    private void runCommand(ArrayList<String> tokens) {
        ViewState currentState = ViewState.getCurrentState();
        String errorMessage = "";
        String command;

        try {
            command = tokens.removeFirst().trim();
        } catch (NoSuchElementException e) {
            //The user simply pressed enter, nothing to print as error for readability.
            return;
        }

        try {
            switch (command.toLowerCase()) {
                case "setnickname", "sn" -> currentState.setNickname(tokens.removeFirst());
                case "createlobby", "cl" -> {
                    errorMessage = "expected a valid maximum number of players for the lobby as first argument";
                    currentState.createLobby(Integer.parseInt(tokens.removeFirst()));
                }
                case "joinlobby", "jl" -> {
                    errorMessage = "expected a valid lobbyUUID as first argument";
                    currentState.joinLobby((UUID.fromString(tokens.removeFirst())));
                }
                case "selectcolor", "sc" -> currentState.selectColor(convertColor(tokens.removeFirst()));
                case "leavelobby", "ll" -> currentState.leaveLobby();
                case "pickinitial", "pi" ->
                        currentState.placeCard(new GenericPair<>(0, 0), 1, convertSide(tokens.removeFirst()));
                case "placecard", "pc" ->
                    currentState.placeCard(
                            new GenericPair<>(Integer.parseInt(tokens.removeFirst()), Integer.parseInt(tokens.removeFirst())),
                            Integer.parseInt(tokens.removeFirst()),
                            convertSide(tokens.removeFirst())
                    );
                case "pickobjective", "po" -> currentState.pickObjective(Integer.parseInt(tokens.removeFirst()));
                case "drawfromdeck", "dfd" -> currentState.drawFromDeck(tokens.removeFirst());
                case "drawfromvisiblecards", "dfvc" -> {
                    errorMessage = "expected valid position to draw from as first argument";
                    currentState.drawFromVisibleCards(
                            tokens.removeFirst(), Integer.parseInt(tokens.removeFirst())
                    );
                }
                case "broadcastmessage", "bm" -> {
                    String message = tokens.stream().reduce("", (a, b) -> a + " " + b);
                    currentState.broadcastMessage(
                            message.substring(0, Math.min(message.length(), 150))
                    );
                }
                case "directmessage", "dm" -> {
                    String receiverNickname = tokens.removeFirst();
                    String message = tokens.stream().reduce("", (a, b) -> a + " " + b);
                    currentState.directMessage(
                            receiverNickname, message.substring(0, Math.min(message.length(), 150))
                    );
                }
                case "showfield", "sf" -> currentState.showField(Integer.parseInt(tokens.removeFirst()));
                case "movefield", "mf" -> currentState.moveField(
                        new GenericPair<>(Integer.parseInt(tokens.removeFirst()), Integer.parseInt(tokens.removeFirst()))
                );
                case "quit" -> currentState.quit();
                case "ok" -> currentState.toLobbies();
                default -> throw new ForbiddenActionException();
            }
        } catch (NoSuchElementException e) {
            ViewState.printError(new NoSuchElementException("Invalid command format: not enough parameters received"));
        } catch (IllegalArgumentException e) {
            if(!e.getMessage().isEmpty()) errorMessage = e.getMessage();
            ViewState.printError(new IllegalArgumentException("Invalid parameters given: " + errorMessage));
        } catch (ForbiddenActionException e) {
            ViewState.printError(new ForbiddenActionException("Unknown command received: maybe check for misspelling?"));
        }
    }

    /**
     * Parses the input command string into tokens and executes the corresponding command.
     *
     * @param input The input command string.
     */
    public void parseCommand(String input) {
        if (input == null) return;

        String delimiters = " ";
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, delimiters);

        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        runCommand(tokens);
    }


    /**
     * Converts the input string to a {@link Side} enum.
     *
     * @param input The input string representing a side.
     * @return The corresponding Side enum.
     * @throws IllegalArgumentException If the input is not a valid side.
     */
    private Side convertSide(String input) {
        return switch(input.trim().toLowerCase()){
            case "front" -> Side.FRONT;
            case "back" -> Side.BACK;
            default -> throw new IllegalArgumentException("expected a valid side as argument, not " + input);
        };
    }

    /**
     * Converts the input string to a {@link Color} enum.
     *
     * @param input The input string representing a color.
     * @return The corresponding Color enum.
     * @throws IllegalArgumentException If the input is not a valid color.
     */
    private Color convertColor(String input) {
        return Arrays.stream(Color.values())
                .filter((color) -> color.name().equalsIgnoreCase(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("expected a valid color as first argument, not " + input));
    }
}
