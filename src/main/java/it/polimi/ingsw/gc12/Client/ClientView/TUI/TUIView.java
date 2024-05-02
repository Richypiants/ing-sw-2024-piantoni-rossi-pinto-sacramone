package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
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
        ClientController.getInstance().viewState.keyPressed();
    }

    private String readUntil(Ansi prompt, List<String> validInput) {
        String selection;
        do{
            clearTerminal();
            printToPosition(ansi().a(prompt));
            selection = console.readLine().trim().toLowerCase();
        } while(!validInput.contains(selection));

        return selection;
    }

    public void connectToServerScreen() {
        /*String language = readUntil(
                ansi().cursor(1, 1).a("Scegli la lingua (Italiano/IT - English/EN): "),
                List.of("italiano", "english", "it", "en")
        );
         */
        String communicationTechnology = readUntil(
                ansi().cursor(1, 1).a("Scegli la tecnologia di comunicazione (RMI-Socket): "),
                List.of("rmi", "socket")
        );
        clearTerminal();
        printToPosition(ansi().cursor(1,1).a("Scegli il tuo nickname: "));
        String nickname = console.readLine();
        printToPosition(ansi().cursor(2,1).a("Connessione al server in corso..."));

        ClientController.getInstance().viewState.connect(communicationTechnology, nickname);
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
                            'setNickname <newNickname>' per cambiare il proprio nickname,
                            'leaveLobby' per lasciare la lobby in cui si e' attualmente,
                            'quit' per ritornare alla schermata iniziale
                            -------------------------------------------------------------
                            'broadcastMessage <message>' per inviare un messaggio in gioco,
                            'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco
                            ------------------------------------------------------------- 
                            'placeCard <x> <y> <inHandPosition> <side>' (x,y): coordinate di piazzamento, inHandPosition: indice di carta, side: lato scelto [front]|[back]
                            'pickObjective <selection>' [1]|[2] per selezionare il proprio obiettivo segreto,
                            'drawFromDeck <deck>' [resource][gold] per pescare una carta coperta dal deck di carte risorsa|oro,
                            'drawFromVisibleCards <deck> <position>' [resource][gold] [1][2] per pescare una carta scoperta dal deck di carte risorsa|oro.
                """
                //TODO: leaveLobby andrebbe promptato solo dopo
                //FIXME: al momento tutti gli usages dei comandi sono stampati qui,
                // quelli in gioco vanno spostati nella schermata di gioco
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
        //FIXME: decomment when field is correctly printed (even when empty)
        showField();
        showHand();
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
        int column = 20;
        for(var card : ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getPlacedResources()) {
            printToPosition(ansi().cursor(10, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        column = 20;
        printToPosition(ansi().cursor(18, 3).a("Gold cards:"));
        for(var card : ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getPlacedGold()){
            printToPosition(ansi().cursor(16, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        column = 20;
        printToPosition(ansi().cursor(24, 3).a("Objective cards:"));
        for(var card : ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCommonObjectives()){
            //printToPosition(ansi().cursor(22, column).a(card.standardAnsi(Side.FRONT)));
            column += 6;
        }

        printToPosition(ansi().cursor(20, 64).a("Secret objective:"));
        //printBlueCard(ansi().cursor(22, 64).a(((ClientGame) ClientController.getInstance.currentLobbyOrGame)getOwnObjective().standardAnsi(Side.FRONT);
    }

    public void updateChat() {
        List<String> chatLog = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getChatLog();
        printToPosition(ansi().cursor(42, 2).bold().a("Last chat messages: ").reset());
        for (int i = 0; i < 3; i++)
            printToPosition(ansi().cursor(43 + i, 4).eraseLine()
                    .a((chatLog.size() >= 3 - i) ? chatLog.get(chatLog.size() - 3 + i) : "")
            );
    }

    public Ansi standardAnsi(ClientCard card, Side side) {
        if(card == null) return Ansi.ansi();

        Ansi sprite = Ansi.ansi();
        for (var line : card.TUI_SPRITES.get(side)) {
            for (var triplet : line) {
                if (triplet.getY()[0] != -1)
                    sprite = sprite.fg(triplet.getY()[0]);
                if (triplet.getY()[1] != -1)
                    sprite = sprite.bg(triplet.getY()[1]);

                for (int i = 0; i < triplet.getZ(); i++)
                    sprite.a(triplet.getX());

                sprite = sprite.reset();
            }
            sprite.cursorMove(-13, 1);
        }

        return sprite;
    }

    public Ansi upscaledAnsi(ClientCard card, Side side) {
        if(card == null) return Ansi.ansi();

        Ansi sprite = Ansi.ansi();
        Ansi[] tmp = new Ansi[3];
        for (var line : card.TUI_SPRITES.get(side)) {
            for (int i = 0; i < 3; i++)
                tmp[i] = Ansi.ansi();

            for (var triplet : line) {
                for (int i = 0; i < 3; i++) {
                    if (triplet.getY()[0] != -1)
                        tmp[i] = tmp[i].fg(triplet.getY()[0]);
                    if (triplet.getY()[1] != -1)
                        tmp[i] = tmp[i].bg(triplet.getY()[1]);
                }

                for (int i = 0; i < triplet.getZ(); i++)
                    if (triplet.getX().charAt(0) != ' ') {
                        tmp[0].a("   ");
                        tmp[1].a(" " + triplet.getX().charAt(0) + " ");
                        tmp[2].a("   ");
                    } else
                        for (int j = 0; j < 3; j++)
                            tmp[j].a("   ");

                for (int i = 0; i < 3; i++) {
                    tmp[i].reset();
                }
            }
            sprite.a(tmp[0]).cursorMove(-39, 1).a(tmp[1]).cursorMove(-39, 1).a(tmp[2]).cursorMove(-39, 1);
        }

        return sprite;
    }

    public void showInitialCardsChoice() {
        ClientCard card = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand().getFirst();
        printToPosition(ansi().cursor(15, 120).bold().eraseLine(Erase.FORWARD)
                .a("Choose which side you want to play your assigned initial card on: ").reset());
        printToPosition(ansi().cursor(20, 120).a(upscaledAnsi(card, Side.FRONT)));
        printToPosition(ansi().cursor(20, 180).a(upscaledAnsi(card, Side.BACK)));
    }

    @Override
    public void showObjectiveCardsChoice() {
        ClientCard card = null;

        printToPosition(ansi().cursor(15, 120).bold().eraseLine(Erase.FORWARD)
                .a("Choose which card you want to keep as your secret objective: ").reset());
        card = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand().get(3);
        printToPosition(ansi().cursor(20, 120).a(standardAnsi(card, Side.FRONT)));
        card = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand().get(4);
        printToPosition(ansi().cursor(20, 180).a(standardAnsi(card, Side.FRONT)));
    }

    @Override
    public void showField() {
        //FIXME: erase old field?
        //TODO: write the correct print function, that iterates all over the field
        final int FIELD_LENGTH = 100;
        final int FIELD_HEIGHT = 100;
        final int FIELD_TOP_LEFT_X = 5;
        final int FIELD_TOP_LEFT_Y = 100;
        final int FIELD_CENTER_X = FIELD_TOP_LEFT_X + FIELD_LENGTH/2;
        final int FIELD_CENTER_Y = FIELD_TOP_LEFT_Y + FIELD_HEIGHT/2;

        final int CARD_LENGTH = 13;
        final int CARD_HEIGHT = 5;

        /**
         *
         *
         *
         * Algorithm:
         *
         * search minX, minY, maxX maxY
         * calculate the avg. values of x,y
         *
         * The initial card will be printed at the DeltaX, DeltaY position of the medium point
         *
         * **/

        GenericPair<ClientCard, Side> placedCardAndSide = ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getThisPlayer().getPlacedCards().get(new GenericPair<>(0,0));
        printToPosition( ansi().cursor(18, 124).a(standardAnsi(placedCardAndSide.getX(), placedCardAndSide.getY())));

        /*
        printRedCard(ClientController.getInstance().cardsList.get(81), Side.FRONT, ansi().cursor(18, 102));
        printBlueCard(ansi().cursor(15, 113));
        printPurpleCard(ansi().cursor(21, 113));
        printGreenCard(ansi().cursor(18, 124));
        printRedCard(ClientController.getInstance().cardsList.get(81), Side.FRONT, ansi().cursor(24, 124));*/
    }

    @Override
    public void showHand() {
        //FIXME: erase or overwrite old hand/cards?
        int column = 10;
        printToPosition(ansi().cursor(28, 2).bold().a("Your hand: ").reset());
        printToPosition(ansi().cursor(32, 3).a("Front:"));
        printToPosition(ansi().cursor(38, 3).a("Back:"));
        for (var card : ((ClientGame) ClientController.getInstance().currentLobbyOrGame).getCardsInHand()) {
            printToPosition(ansi().cursor(30, column).a(standardAnsi(card, Side.FRONT)));
            printToPosition(ansi().cursor(36, column).a(standardAnsi(card, Side.BACK)));
            column += 20;
        }
    }

    public void printRedCard(ClientCard card, Side side, Ansi position) {
        System.out.print(position);
        printToPosition(position.a(standardAnsi(card, side)));
        //System.out.print(position.a("┌─────────────┐").reset());
        //System.out.print(position.a("M").bg(Ansi.Color.RED).a("    ").reset().a("2 S").bg(Ansi.Color.RED).a("    ").reset().a("M").reset());
        //System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.RED).a("             ").reset());
        //System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.RED).a("     ").reset().a("MMM").bg(Ansi.Color.RED).a("     ").reset());
        //System.out.print(ansi().cursorMove(-13, 1).a("").bg(Ansi.Color.RED).a("             ").reset());
        //System.out.print(ansi().cursorMove(-13, 1).a("M").bg(Ansi.Color.RED).a("   ").reset().a("MMMMG").bg(Ansi.Color.RED).a("   ").reset().a("M").reset());
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