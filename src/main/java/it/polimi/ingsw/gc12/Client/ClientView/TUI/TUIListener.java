package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import static org.fusesource.jansi.Ansi.ansi;

public class TUIListener {

    private static TUIListener SINGLETON_TUI_LISTENER;

    private TUIListener() {
    }

    public static TUIListener getInstance() {
        if (SINGLETON_TUI_LISTENER == null)
            SINGLETON_TUI_LISTENER = new TUIListener();
        return SINGLETON_TUI_LISTENER;
    }

    public void startReading() {
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                //FIXME: devo salvare la precedente posizione del cursore?
                System.out.println(ansi().cursor(20, 1));
                parseCommand(scanner.nextLine());
            }
        }
        ).start();
    }

    private void runCommand(ArrayList<String> tokens) {
        //TODO: Search if a more clean strategy exists

        ViewState currentState = ViewState.getCurrentState();

        //TODO: Every case has to check type parameters before calling the State method or eventually die
        switch (tokens.removeFirst().trim()) {
            case "keyPressed" -> currentState.keyPressed();
            case "setNickname" -> currentState.setNickname(tokens.getFirst());
            /*case "broadcastMessage" -> currentState.;
            case "createLobby" -> currentState.;
            case "createPlayer" -> currentState.;
            case "directMessage" -> currentState.;
            case "drawFromDeck" -> currentState;
            case "drawFromVisibleCards" -> currentState.;
            case "joinLobby" -> currentState.;
            case "keepAlive" -> currentState.;
            case "leaveGame" -> currentState.;
            case "leaveLobby" -> currentState.;
            case "pickObjective" -> currentState.;
            case "placeCard" -> currentState.;
            case "setNicknameCommand" -> currentState.;*/
            default -> System.err.println("Unknown command");
        }
        ;
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

}
