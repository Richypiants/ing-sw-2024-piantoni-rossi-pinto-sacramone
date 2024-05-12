package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.fusesource.jansi.Ansi;

import java.io.Console;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.UUID;

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
            switch (command) {
                case "setNickname" -> currentState.setNickname(tokens.removeFirst());
                case "createLobby" -> {
                    errorMessage = "expected numero di giocatori nella lobby as first argument";
                    currentState.createLobby(Integer.parseInt(tokens.removeFirst()));
                }
                case "joinLobby" -> {
                    errorMessage = "expected lobbyUUID as second argument";
                    currentState.joinLobby((UUID.fromString(tokens.removeFirst())));
                }
                case "leaveLobby" -> currentState.leaveLobby();
                case "broadcastMessage" -> {
                    currentState.broadcastMessage(
                            tokens.stream().reduce("", (a, b) -> a + " " + b).substring(0, 200)
                    );
                }
                case "directMessage" -> currentState.directMessage(
                        tokens.removeFirst(), tokens.stream().reduce("", (a, b) -> a + " " + b).substring(0, 200)
                );
                case "pickInitial" -> currentState.placeCard(new GenericPair<>(0, 0), 0, convertSide(tokens.removeFirst())) ;
                case "placeCard" ->
                    currentState.placeCard(
                            new GenericPair<>(Integer.parseInt(tokens.removeFirst()), Integer.parseInt(tokens.removeFirst())),
                            Integer.parseInt(tokens.removeFirst()) - 1,
                            convertSide(tokens.removeFirst())
                    );
                case "pickObjective" -> currentState.pickObjective(Integer.parseInt(tokens.removeFirst()));
                case "drawFromDeck" -> currentState.drawFromDeck(tokens.removeFirst());
                case "drawFromVisibleCards" -> {
                    errorMessage = "expected position to draw from as second argument";
                    currentState.drawFromVisibleCards(
                            tokens.removeFirst(), Integer.parseInt(tokens.removeFirst())
                    );
                }
                case "quit" -> currentState.quit();
                default -> System.out.println("Unknown command");
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
}
