package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.CreatePlayerCommand;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.Erase;
import static org.fusesource.jansi.Ansi.ansi;

public class TUIView extends View {

    private static TUIView SINGLETON_TUI_INSTANCE = null;
    private final ExecutorService singleThreadExecutor;
    private final TUIListener listener;
    private final Scanner scanner = new Scanner(System.in);

    private TUIView() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        listener = TUIListener.getInstance();
    }

    public static TUIView getInstance() {
        if (SINGLETON_TUI_INSTANCE == null)
            SINGLETON_TUI_INSTANCE = new TUIView();
        return SINGLETON_TUI_INSTANCE;
    }

    public static void clearTerminal() {
        System.out.println(ansi().cursor(1, 1).eraseScreen(Erase.FORWARD));
    }

    //TODO: currently erasing already written input chars
    public static void printDebug(Ansi toPrint) {
        System.out.print(ansi().cursor(17, 1));
        System.out.print(toPrint);
        System.out.println(ansi().reset().cursor(20, 1).eraseLine(Erase.FORWARD));
        //FIXME: autoResetting... should keep it?
    }

    //TODO: currently erasing already written input chars
    public void printToPosition(Ansi toPrint) {
        System.out.print(toPrint);
        System.out.println(ansi().reset().cursor(20, 1).eraseScreen(Erase.FORWARD));
        //FIXME: autoResetting... should keep it?
    }

    public void titleScreen() {
        clearTerminal();
        printToPosition(ansi().cursor(1, 1).a("Starting Codex Naturalis..."));
        try {
            sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printToPosition(ansi().cursor(2, 1).a("Cranio Creations Logo"));
        try {
            sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printToPosition(ansi().cursor(3, 1).a("Codex Naturalis Logo"));
        printToPosition(ansi().cursor(4, 1));
        printToPosition(ansi().cursor(5, 1).a("Premi Invio per iniziare..."));
        scanner.nextLine();

    }

    private String readUntilRoutine(Ansi prompt, List<String> validInput){
        String selection;
        do{
            clearTerminal();
            printToPosition(ansi().a(prompt));
            selection = scanner.nextLine();
        } while(!validInput.contains(selection));

        return selection;
    }

    public void connectToServerScreen() {
        String language = readUntilRoutine(
                ansi().cursor(1, 1).a("Scegli la lingua (Italiano-English): "),
                List.of("Italiano", "English")
        );
        String communicationTechnology = readUntilRoutine(
                ansi().cursor(1, 1).a("Scegli la tecnologia di comunicazione (RMI-Socket): "),
                List.of("RMI", "Socket")
        );
        ClientController.getInstance().setCommunicationTechnology(communicationTechnology);
        clearTerminal();
        printToPosition(ansi().cursor(1,1).a("Scegli il tuo nickname: "));
        String nickname = scanner.nextLine();
        printToPosition(ansi().cursor(2,1).a("Connessione al server in corso..."));
        try {
            ClientController.getInstance().serverConnection
                    .requestToServer(ClientController.getInstance().thisClient, new CreatePlayerCommand(nickname));
        } catch (Exception e) {
            //TODO: Logging to terminal in another position
            e.printStackTrace();
            //System.err.println("xxx Exception");
            //printError();

            //TODO: What to do if connection failed? Automatically retry connection, go into another state,
            // redo the initial config(unlikely option)
        }
    }

    public void lobbyScreen() {
        clearTerminal();

        int i = 1;
        printToPosition(ansi().cursor(i++,1)
                .fg(Ansi.Color.RED).bold()
                .a("[PLAYER]: ").a(ClientController.getInstance().ownNickname));
        printToPosition(ansi().cursor(i++, 1).a("[CURRENT LOBBY]: " + (
                ClientController.getInstance().currentLobbyOrGame == null ?
                        "none" :
                        ClientController.getInstance().currentLobbyOrGame) //TODO: stampare UUID?
                )
        );
        listener.startReading();
        printToPosition(ansi().cursor(i++, 1).a("[ACTIVE LOBBIES] "));
        for (var entry : ClientController.getInstance().lobbies.entrySet()) {
            printToPosition(ansi().cursor(i++, 1).a(entry.getKey() + ": " + entry.getValue()));
        }
        printToPosition(ansi().cursor(i++, 1));
        printToPosition(ansi().cursor(i, 1).a(
                """
                           'createLobby <maxPlayers>' per creare una lobby,
                        'joinLobby <lobbyUUID>' per joinare una lobby esistente,
                           'setNickname <newNickname>' per cambiare il proprio nickname:
                        """
        ));
    }

    public void gameScreen() {

    }

    public void connectedConfirmation(){
        printToPosition(ansi().cursor(3,1).a("Connessione al server riuscita: nickname confermato!"));
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            //Shouldn't happen
        }
    }

    public void updateNickname(){
        //TODO: eraseForward potrebbe funzionare? Se sì, scrivere due print
        System.out.println(ansi()
                .fg(Ansi.Color.RED).bold()
                .cursor(1, 11).a(ClientController.getInstance().ownNickname).eraseLine().reset());
    }

    /*
    1) Title screen (Cranio Creations + Codex Naturalis + Premi un tasto per iniziare...)
    (+ da qualche parte choose language / connection technology)
    2) Choose a nickname + Connecting to server...
    3) Lobbies menu
    4) Game view
     */
}