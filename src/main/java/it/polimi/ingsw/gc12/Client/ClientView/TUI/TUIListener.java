package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import org.fusesource.jansi.Ansi;

import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class TUIListener {

    private static TUIListener SINGLETON_TUI_LISTENER;
    public static int COMMAND_INPUT_ROW = 48;
    public static int EXCEPTIONS_ROW = 50;

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
                System.out.print(ansi().cursor(COMMAND_INPUT_ROW, 3).eraseScreen(Ansi.Erase.FORWARD));
                parseCommand(scanner.nextLine());
            }
        }
        ).start();
    }

    private void runCommand(ArrayList<String> tokens) {
        //TODO: Search if a more clean strategy exists

        ViewState currentState = ClientController.getInstance().viewState;

        //TODO: Every case has to check type parameters before calling the State method or eventually die

        try {
            switch (tokens.removeFirst().trim()) {
                case "setNickname" -> currentState.setNickname(tokens.getFirst());
                case "createLobby" -> currentState.createLobby(Integer.parseInt(tokens.getFirst()));
                case "joinLobby" -> currentState.joinLobby((UUID.fromString(tokens.getFirst())));
                case "leaveLobby" -> currentState.leaveLobby();
                case "broadcastMessage" -> currentState.broadcastMessage(
                        tokens.stream().reduce("", (a, b) -> a + " " + b)
                );
                case "directMessage" -> currentState.directMessage(
                        tokens.removeFirst(), tokens.stream().reduce("", (a, b) -> a + " " + b)
                );
            /*
            case "drawFromDeck" -> currentState;
            case "drawFromVisibleCards" -> currentState.;
            case "keepAlive" -> currentState.;
            case "leaveGame" -> currentState.;
            case "pickObjective" -> currentState.;
            case "placeCard" -> currentState.;
            */
                default -> System.out.println("Unknown command");
            }
        } catch (NoSuchElementException e) {
            //TODO: stampare "Formato del comando fornito non valido"
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            //TODO: stampare "Parametri forniti invalidi"
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

}
