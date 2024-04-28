package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import org.fusesource.jansi.Ansi;

import java.io.Console;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.Erase;
import static org.fusesource.jansi.Ansi.ansi;

public class TUIView extends View {

    private static TUIView SINGLETON_TUI_INSTANCE = null;
    private final TUIListener listener;
    private static /*(?)*/ final Console console = System.console();

    private TUIView() {
        listener = TUIListener.getInstance();
    }

    public static TUIView getInstance() {
        if (SINGLETON_TUI_INSTANCE == null)
            SINGLETON_TUI_INSTANCE = new TUIView();
        return SINGLETON_TUI_INSTANCE;
    }

    public static void clearTerminal() {
        System.out.print(ansi().cursor(1, 1)
                .eraseScreen(Erase.FORWARD).eraseScreen(Erase.BACKWARD)
                .cursor(TUIListener.COMMAND_INPUT_ROW - 1, 1).a("------------------------------------------------------------------")
                .cursor(TUIListener.COMMAND_INPUT_ROW, 1)
                .a("> [" + ClientController.getInstance().ownNickname + "] ")
        );
    }

    //TODO: currently erasing already written input chars
    @Override
    public void printError(Throwable error) {
        System.out.print(ansi().cursor(TUIListener.EXCEPTIONS_ROW, 1)
                .a(error.getMessage()).reset()
                .cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN).eraseLine(Erase.FORWARD)
        );
        //FIXME: autoResetting... should keep it?
    }

    //TODO: currently erasing already written input chars
    public void printToPosition(Ansi toPrint) {
        //FIXME: save and restoreCursorPosition are better?
        //System.out.print(ansi().saveCursorPosition());
        System.out.print(toPrint);
        System.out.print(ansi().reset()
                .cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN).eraseScreen(Erase.FORWARD)
        );
        //FIXME: autoResetting... should keep it?
    }

    public void titleScreen() {
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().ownNickname.length();
        clearTerminal();

        printToPosition(ansi().cursor(1, 1).a("Starting Codex Naturalis..."));
        try {
            sleep(500);
        } catch (Exception e) {
            ClientController.getInstance().errorLogger.log(e);
        }
        printToPosition(ansi().cursor(2, 1).a("Cranio Creations Logo"));
        try {
            sleep(500);
        } catch (Exception e) {
            ClientController.getInstance().errorLogger.log(e);
        }
        printToPosition(ansi().cursor(3, 1).a("Codex Naturalis Logo"));
        printToPosition(ansi().cursor(4, 1));
        printToPosition(ansi().cursor(5, 1).a("Premi Invio per iniziare..."));
        console.readLine();
    }

    private String readUntil(Ansi prompt, List<String> validInput) {
        String selection;
        do{
            clearTerminal();
            printToPosition(ansi().a(prompt));
            selection = console.readLine();
        } while(!validInput.contains(selection.trim().toLowerCase()));

        return selection;
    }

    public String connectToServerScreen() {
        String language = readUntil(
                ansi().cursor(1, 1).a("Scegli la lingua (Italiano/IT - English/EN): "),
                List.of("italiano", "english", "it", "en")
        );
        String communicationTechnology = readUntil(
                ansi().cursor(1, 1).a("Scegli la tecnologia di comunicazione (RMI-Socket): "),
                List.of("rmi", "socket")
        );
        ClientController.getInstance().setCommunicationTechnology(communicationTechnology);
        clearTerminal();
        printToPosition(ansi().cursor(1,1).a("Scegli il tuo nickname: "));
        String nickname = console.readLine();
        printToPosition(ansi().cursor(2,1).a("Connessione al server in corso..."));

        return nickname;
    }

    public void connectedConfirmation() {
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().ownNickname.length();
        printToPosition(ansi().cursor(3, 1).a("Connessione al server riuscita: nickname confermato!"));
        listener.startReading();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            ClientController.getInstance().errorLogger.log(e);
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
                            'leaveLobby' per lasciare la lobby in cui si e' attualmente
                        """ //TODO: leaveLobby andrebbe promptato solo dopo
        ));
    }

    public void updateNickname(){
        //TODO: eraseForward potrebbe funzionare? Se sì, scrivere due print
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().ownNickname.length();
        System.out.print(ansi()
                .fg(Ansi.Color.RED).bold()
                .cursor(1, 11).a(ClientController.getInstance().ownNickname).eraseLine().reset()
                .cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN));
    }

    public void gameScreen() {
        clearTerminal();

        printStatsTable();
        printOpponentsFieldsMiniaturized();
        printCommonPlacedCards();
        printPlayerHand();
        printPlayerField();
        updateChat();
    }

    public void printStatsTable() {
        int i = 2;
        printToPosition(ansi().cursor(i++, 23)
                .bold().a("Points | ").reset()
                .fg(Ansi.Color.RED).a("Mushroom [M]").reset()
                .a(" | ")
                .fg(Ansi.Color.GREEN).a("Grass [G]").reset()
                .a(" | ")
                .fg(Ansi.Color.BLUE).a("Wolf [W]").reset()
                .a(" | ")
                .fg(Ansi.Color.MAGENTA).a("Butterfly [B]").reset()
                .fg(94).a(" | Scroll [S] | Potion [P] | Feather [F]").reset()
        );

        for (var player : ClientController.getInstance().currentLobbyOrGame.getPlayers())
            printToPosition(ansi().cursor(i, 2).a("[#" + (i - 2) + "] ").a(player.getNickname())
                    .cursor(i, 26).a("0")
                    .cursor(i, 38).a("0")
                    .cursor(i, 49).a("0")
                    .cursor(i, 59).a("0")
                    .cursor(i, 70).a("0")
                    .cursor(i, 83).a("0")
                    .cursor(i, 94).a("0")
                    .cursor(i++, 105).a("0")
            );
    }

    public void printOpponentsFieldsMiniaturized() {

    }

    public void printCommonPlacedCards() {
        //FIXME: erase or overwrite old placed cards?
        printToPosition(ansi().cursor(8, 2).bold().a("Common placed cards: ").reset());
        printToPosition(ansi().cursor(12, 3).a("Resource cards:"));
        printToPosition(ansi().cursor(18, 3).a("Gold cards:"));
        printToPosition(ansi().cursor(24, 3).a("Objective cards:"));
        printRedCard(ansi().cursor(10, 20));
        printGreenCard(ansi().cursor(16, 20));
        printGreenCard(ansi().cursor(10, 40));
        printBlueCard(ansi().cursor(16, 40));
        printRedCard(ansi().cursor(22, 20));
        printBlueCard(ansi().cursor(22, 40));
        printToPosition(ansi().cursor(20, 64).a("Secret objective:"));
        printBlueCard(ansi().cursor(22, 64));
    }

    public void printPlayerHand() {
        //FIXME: erase or overwrite old hand/cards?
        printToPosition(ansi().cursor(28, 2).bold().a("Your hand: ").reset());
        printToPosition(ansi().cursor(32, 3).a("Front:"));
        printToPosition(ansi().cursor(38, 3).a("Back:"));
        printRedCard(ansi().cursor(30, 10));
        printRedCard(ansi().cursor(36, 10));
        printGreenCard(ansi().cursor(30, 30));
        printGreenCard(ansi().cursor(36, 30));
        printBlueCard(ansi().cursor(30, 50));
        printBlueCard(ansi().cursor(36, 50));
    }

    public void printPlayerField() {
        //FIXME: erase old field?
        printRedCard(ansi().cursor(18, 102));
        printBlueCard(ansi().cursor(15, 113));
        printPurpleCard(ansi().cursor(21, 113));
        printGreenCard(ansi().cursor(18, 124));
        printRedCard(ansi().cursor(24, 124));
    }

    public void updateChat() {
        List<String> chatLog = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getChatLog();
        printToPosition(ansi().cursor(42, 2).bold().a("Last chat messages: ").reset());
        for (int i = 0; i < 3; i++)
            printToPosition(ansi().cursor(43 + i, 4).eraseLine()
                    .a((chatLog.size() >= 3 - i) ? chatLog.get(chatLog.size() - 3 + i) : "")
            );
    }

    public void printRedCard(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.a("M").bg(Ansi.Color.RED).a("    ").reset().a("2 S").bg(Ansi.Color.RED).a("    ").reset().a("M").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.RED).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.RED).a("     ").reset().a("MMM").bg(Ansi.Color.RED).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.RED).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("M").bg(Ansi.Color.RED).a("   ").reset().a("MMMMG").bg(Ansi.Color.RED).a("   ").reset().a("M").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
        /*System.out.println("""
                ┌────┬───────┬────┬────┬────┬───────┬────┐
                │    │       │    │    │    │       │    │
                ├────┘       └────┘    └────┘       └────┤
                │            ┌────┬────┬────┐            │
                │            │    │    │    │            │
                │            └────┴────┴────┘            │
                ├────┐  ┌────┬────┬────┬────┬────┐  ┌────┤
                │    │  │    │    │    │    │    │  │    │
                └────┴──┴────┴────┴────┴────┴────┴──┴────┘
                """);*/
    }

    public void printBlueCard(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.a("W").bg(Ansi.Color.BLUE).a("    ").reset().a("2 S").bg(Ansi.Color.BLUE).a("    ").reset().a("M").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.BLUE).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.BLUE).a("     ").reset().a("MMM").bg(Ansi.Color.BLUE).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.BLUE).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("M").bg(Ansi.Color.BLUE).a("   ").reset().a("MMMMG").bg(Ansi.Color.BLUE).a("   ").reset().a("M").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
    }

    public void printPurpleCard(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.a("W").bg(Ansi.Color.MAGENTA).a("    ").reset().a("2 S").bg(Ansi.Color.MAGENTA).a("    ").reset().a("M").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.MAGENTA).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.MAGENTA).a("     ").reset().a("MMM").bg(Ansi.Color.MAGENTA).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.MAGENTA).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("M").bg(Ansi.Color.MAGENTA).a("   ").reset().a("MMMMG").bg(Ansi.Color.MAGENTA).a("   ").reset().a("M").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
    }

    public void printGreenCard(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.a("W").bg(Ansi.Color.GREEN).a("    ").reset().a("2 S").bg(Ansi.Color.GREEN).a("    ").reset().a("M").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.GREEN).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.GREEN).a("     ").reset().a("MMM").bg(Ansi.Color.GREEN).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.GREEN).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("M").bg(Ansi.Color.GREEN).a("   ").reset().a("MMMMG").bg(Ansi.Color.GREEN).a("   ").reset().a("M").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
    }

    public void printRedCard2(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.bg(196).a(" ").bg(88).a("    ").reset().a("2 S").bg(88).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(88).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(88).a("     ").bg(196).a("   ").bg(88).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(88).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).bg(196).a(" ").bg(88).a("   ").bg(196).a("    ").bg(82).a(" ").bg(88).a("   ").reset().bg(196).a(" ").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
        /*System.out.println("""
                ┌────┬───────┬────┬────┬────┬───────┬────┐
                │    │       │    │    │    │       │    │
                ├────┘       └────┘    └────┘       └────┤
                │            ┌────┬────┬────┐            │
                │            │    │    │    │            │
                │            └────┴────┴────┘            │
                ├────┐  ┌────┬────┬────┬────┬────┐  ┌────┤
                │    │  │    │    │    │    │    │  │    │
                └────┴──┴────┴────┴────┴────┴────┴──┴────┘
                """);*/
    }

    public void printBlueCard2(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.bg(32).a(" ").bg(20).a("    ").reset().a("2 S").bg(20).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(20).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(20).a("     ").bg(32).a("   ").bg(20).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(20).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).bg(32).a(" ").bg(20).a("   ").bg(196).a("    ").bg(30).a(" ").bg(20).a("   ").reset().bg(32).a(" ").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
    }

    public void printPurpleCard2(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.bg(207).a(" ").bg(127).a("    ").reset().a("2 S").bg(127).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(127).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(127).a("     ").bg(207).a("   ").bg(127).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(127).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).bg(207).a(" ").bg(127).a("   ").bg(196).a("    ").bg(82).a(" ").bg(127).a("   ").reset().bg(207).a(" ").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
    }

    public void printGreenCard2(/*ClientCard card, */Ansi position) {
        //System.out.print(position.a("┌─────────────┐").reset());
        System.out.print(position.bg(82).a(" ").bg(22).a("    ").reset().a("2 S").bg(22).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(22).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(22).a("     ").bg(82).a("   ").bg(22).a("     ").reset());
        System.out.print(ansi().cursorMove(-13, 1).a("").bg(22).a("             ").reset());
        System.out.print(ansi().cursorMove(-13, 1).bg(82).a(" ").bg(22).a("   ").bg(82).a("    ").bg(196).a(" ").bg(22).a("   ").reset().bg(82).a(" ").reset());
        //System.out.print(ansi().cursorMove(-15, 1).a("└─────────────┘").reset());
    }

    /*
    1) Title screen (Cranio Creations + Codex Naturalis + Premi un tasto per iniziare...)
    (+ da qualche parte choose language / connection technology)
    2) Choose a nickname + Connecting to server...
    3) Lobbies menu
    4) Game view
     */
}