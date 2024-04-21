package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ClientController.TUIListener;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.Erase;
import static org.fusesource.jansi.Ansi.ansi;

public class TUIView extends View {

    //private static Scanner scanner;
    private static ExecutorService singleThreadExecutor;

    @Override
    public void addListener() {

    }

    @Override
    public void initializeApp() {
        System.out.println("Game initialization...");
    }

    @Override
    public void show() {

    }

    @Override
    public void input() {

    }

    public static void clearTerminal() {
        System.out.println(ansi().cursor(1, 1).eraseScreen(Erase.FORWARD));
    }

    //TODO: currently erasing already written input chars
    public static void printToPosition(Ansi toPrint) {
        System.out.print(toPrint);
        System.out.println(ansi().reset().cursor(20, 1).eraseScreen(Erase.FORWARD)); //FIXME: autoResetting... should keep it?
    }

    //TODO: handle exception
    public static void main(String[] args) {
        TUIListener listener = TUIListener.getInstance();
        listener.startReading();
        AnsiConsole.systemInstall();
        //System.out.println(ansi().fg(Ansi.Color.GREEN).a("Hello").reset());
        //System.out.println(ansi().cursorUpLine().cursorUpLine().bg(Color.RED).a("World!").reset());

        singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.submit(TUIView::printTitleScreen);
    }

    public static void printTitleScreen() {
        printToPosition(ansi().cursor(1, 1).a("Starting Codex Naturalis..."));
        try {
            sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printToPosition(ansi().cursor(2, 1).a("Cranio Creations Logo"));
        try {
            sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printToPosition(ansi().cursor(3, 1).a("Codex Naturalis Logo"));
        printToPosition(ansi().cursor(4, 1));
        printToPosition(ansi().cursor(5, 1).a("Premi un tasto per iniziare..."));
        //waitforinput();

        clearTerminal();
        singleThreadExecutor.submit(TUIView::connectToServerScreen);
    }

    public static void connectToServerScreen() {
        printToPosition(ansi().cursor(1, 1).a("Scegli il tuo nickname: "));
        //waitforinput();
        printToPosition(ansi().cursor(2, 1).a("Connettendosi al server..."));
        printToPosition(ansi().cursor(3, 1).a("Connessione al server riuscita: nickname confermato!"));
        //sleep(1000);

        clearTerminal();
        singleThreadExecutor.submit(TUIView::lobbiesScreen);
    }

    public static void lobbiesScreen() {
        printToPosition(ansi().cursor(1, 1).a("Here's the list of currently open lobbies:"));
        int i = 2;
        for (var entry : ClientController.getInstance().lobbies.entrySet()) {
            printToPosition(ansi().cursor(i++, 1).a(entry.getKey() + ": " + entry.getValue()));
        }
        printToPosition(ansi().cursor(i++, 1));
        printToPosition(ansi().cursor(i, 1).a(
                """
                        'createLobby' per creare una lobby,
                        'joinLobby <lobbyUUID>' per joinare una lobby esistente,
                        'setNickname <nickname>' per cambiare il proprio nickname:
                        """
        ));
    }

    public static void gameScreen() {

    }

    /*
    1) Title screen (Cranio Creations + Codex Naturalis + Premi un tasto per iniziare...)
    (+ da qualche parte choose language / connection technology)
    2) Choose a nickname + Connecting to server...
    3) Lobbies menu
    4) Game view
     */
}
