package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.fusesource.jansi.Ansi;

import java.io.Console;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class TUIListener {

    private static TUIListener SINGLETON_TUI_LISTENER;
    public static int COMMAND_INPUT_ROW = 48;
    public static int COMMAND_INPUT_COLUMN = 6;
    public static int EXCEPTIONS_ROW = 49;

    private TUIListener() {
    }

    public static TUIListener getInstance() {
        if (SINGLETON_TUI_LISTENER == null)
            SINGLETON_TUI_LISTENER = new TUIListener();
        return SINGLETON_TUI_LISTENER;
    }

    public void startReading() {
        Console console = System.console();
        Thread reader = new Thread(() -> {
            String command = "";
            do {
                try {
                    command = console.readLine();
                } catch (NoSuchElementException ignored) {
                }
                System.out.print(ansi().cursor(COMMAND_INPUT_ROW, COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));
                parseCommand(command);
            } while (!command.equals("quit"));
        }
        );
        reader.setDaemon(false);
        reader.start();
    }

    private void runCommand(ArrayList<String> tokens) {

        ViewState currentState = ClientController.getInstance().viewState;
        String errorMessage = "";
        String command;

        //TODO: Every case has to check type parameters before calling the State method or eventually die

        try {
            command = tokens.removeFirst().trim();
        } catch (NoSuchElementException e) {
            //The user simply pressed enter, nothing to print as error for readability.
            return;
        }

        try {
            switch (command.toLowerCase()) {
                case "setnickname", "sn" -> {
                    String nickname = tokens.removeFirst();
                    //TODO: invece di troncare, printare errore e reimmissione nickname?
                    currentState.setNickname(nickname.substring(0, Math.min(nickname.length(), 10)));
                }
                case "createlobby", "cl" -> {
                    errorMessage = "expected numero di giocatori nella lobby as first argument";
                    currentState.createLobby(Integer.parseInt(tokens.removeFirst()));
                }
                case "joinlobby", "jl" -> {
                    errorMessage = "expected lobbyUUID as second argument";
                    currentState.joinLobby((UUID.fromString(tokens.removeFirst())));
                }
                case "selectcolor", "sc" -> {
                    currentState.selectColor(convertColor(tokens.removeFirst()));
                }
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
                    errorMessage = "expected position to draw from as second argument";
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
                //FIXME: this could be only a TUI function, consider extending this behavior to other functions
                // exclusive to TUI or consider adding this to viewStates too
                case "movefield", "mf" -> currentState.moveField(
                        new GenericPair<>(Integer.parseInt(tokens.removeFirst()), Integer.parseInt(tokens.removeFirst()))
                );
                case "quit" -> currentState.quit();
                case "ok" -> currentState.toLobbies();
                default -> throw new IllegalArgumentException("Unknown command");
            }
        } catch (NoSuchElementException e) {
            ClientController.getInstance().errorLogger.log(new NoSuchElementException("Formato del comando fornito non valido: parametri forniti insufficienti"));
        } catch (IllegalArgumentException e) {
            if(!e.getMessage().isEmpty()) errorMessage = e.getMessage();
            ClientController.getInstance().errorLogger.log(new IllegalArgumentException("Parametri forniti invalidi: " + errorMessage));
        }
    }

    public void parseCommand(String input) {
        String delimiters = " ";
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, delimiters);

        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        runCommand(tokens);
    }

    //TODO: sposterei nello state per single resp principle...
    private Side convertSide(String input) {
        return switch(input.trim().toLowerCase()){
            case "front" -> Side.FRONT;
            case "back" -> Side.BACK;
            default -> throw new IllegalArgumentException("unknown side string given: " + input);
        };
    }

    private Color convertColor(String input) {
        return Arrays.stream(Color.values())
                .filter((color) -> color.name().equalsIgnoreCase(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("colore non valido fornito: " + input));
    }
}
