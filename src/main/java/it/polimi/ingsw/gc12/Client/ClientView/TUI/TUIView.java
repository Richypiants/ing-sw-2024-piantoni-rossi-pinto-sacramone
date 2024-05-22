package it.polimi.ingsw.gc12.Client.ClientView.TUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.Console;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.ToIntBiFunction;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.Erase;
import static org.fusesource.jansi.Ansi.ansi;

public class TUIView extends View {

    private static TUIView SINGLETON_TUI_INSTANCE = null;
    private final TUIListener listener;
    private static /*(?)*/ final Console console = System.console();

    private final GenericPair<Integer, Integer> TERMINAL_SIZE = new GenericPair<>(49, 211); //x: rows, y:columns

    private final GenericPair<Integer, Integer> FIELD_SIZE = new GenericPair<>(38, 105); //x: height, y: width
    private final GenericPair<Integer, Integer> FIELD_TOP_LEFT = new GenericPair<>(10, 105); //x: startingRow, y: startingColumn
    private final GenericPair<Integer, Integer> FIELD_CENTER = new GenericPair<>(
            FIELD_TOP_LEFT.getX() + (FIELD_SIZE.getX() / 2),
            FIELD_TOP_LEFT.getY() + (FIELD_SIZE.getY() / 2)
    );
    private final GenericPair<Integer, Integer> CARD_SIZE = new GenericPair<>(13, 5);
    //Manually computed over examples
    private final GenericPair<Integer, Integer> CURSOR_OFFSET = new GenericPair<>(3, 11);

    private TUIView() {
        AnsiConsole.systemInstall(

        );
        listener = TUIListener.getInstance();
        try {
            //FIXME: on Mac bash instead of cmd (on Linux too?)
            new ProcessBuilder("cmd", "/c", "mode con:cols=" + TERMINAL_SIZE.getY() + " lines=" + TERMINAL_SIZE.getX())
                    .inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TUIView getInstance() {
        if (SINGLETON_TUI_INSTANCE == null)
            SINGLETON_TUI_INSTANCE = new TUIView();
        return SINGLETON_TUI_INSTANCE;
    }

    public static void clearTerminal() {
        System.out.print(ansi()
                .saveCursorPosition()
                .cursor(TUIListener.COMMAND_INPUT_ROW - 2, 1)
                .eraseScreen(Erase.BACKWARD).eraseLine(Erase.FORWARD)
                .cursorDownLine()
                .a("------------------------------------------------------------------").eraseLine(Erase.FORWARD)
                .cursorDownLine()
                .a("> [" + ClientController.getInstance().viewModel.getOwnNickname() + "] ")
                .restoreCursorPosition()
                .eraseScreen(Erase.FORWARD)
        );
    }

    @Override
    public void printError(Throwable error) {
        System.out.print(ansi().saveCursorPosition()
                .cursor(TUIListener.EXCEPTIONS_ROW, 1)
                .a(error.getMessage()).reset()
                .restoreCursorPosition()
        );
        //FIXME: autoResetting... should keep it?
    }

    public void printToPosition(Ansi toPrint) {
        System.out.print(ansi().saveCursorPosition()
                .a(toPrint).reset()
                .restoreCursorPosition()
                .eraseScreen(Erase.FORWARD)
        );
        //FIXME: autoResetting... should keep it?
    }

    private String readUntil(Ansi prompt, List<String> validInput) {
        String selection;
        do {
            clearTerminal();
            System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN)
                    .eraseLine(Erase.FORWARD)
            );
            printToPosition(ansi().a(prompt));
            selection = console.readLine().trim().toLowerCase();
        } while (!validInput.contains(selection));

        return selection;
    }

    public void titleScreen() {
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().viewModel.getOwnNickname().length();
        System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN));
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
        System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));

        ClientController.getInstance().viewState.keyPressed();
    }

    public void connectToServerScreen() {
        clearTerminal();
        /*String language = readUntil(
                ansi().cursor(1, 1).a("Scegli la lingua (Italiano/IT - English/EN): "),
                List.of("italiano", "english", "it", "en")
        );
        clearTerminal();
         */
        printToPosition(ansi().cursor(1, 1).a("Inserisci l'indirizzo IP del server (leave empty for 'localhost'): "));
        String serverIPAddress = console.readLine();
        System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));
        clearTerminal();
        String communicationTechnology = readUntil(
                ansi().cursor(1, 1).a("Scegli la tecnologia di comunicazione (RMI-Socket): "),
                List.of("rmi", "socket")
        );
        System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));
        clearTerminal();

        String nickname = "";
        boolean lastInputWasInvalid = false;
        final int MAX_NICK_LENGTH = 10;

        do {
            if(lastInputWasInvalid)
                printToPosition(ansi().cursor(1, 1).a("Il nickname inserito possiede una lunghezza superiore a " + MAX_NICK_LENGTH + " caratteri oppure e' vuoto!"));
            printToPosition(ansi().cursor(2, 1).a("Inserisci il tuo nickname [Max " + MAX_NICK_LENGTH + " caratteri]: "));
            lastInputWasInvalid = false;
            nickname = console.readLine().trim();
            if(nickname.length() > MAX_NICK_LENGTH || nickname.isEmpty())
                lastInputWasInvalid = true;
            clearTerminal();
            System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN)
                    .eraseLine(Ansi.Erase.FORWARD)
                    .cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN));
        }while(lastInputWasInvalid);

        printToPosition(ansi().cursor(2,1).a("Connessione al server in corso..."));

        ClientController.getInstance().viewState.connect(serverIPAddress, communicationTechnology, nickname);
    }

    public void connectedConfirmation() {
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().viewModel.getOwnNickname().length();
        printToPosition(ansi().cursor(3, 1).a("Connessione al server riuscita: nickname confermato!"));
        System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW, TUIListener.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));
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
                .a("[PLAYER]: ").a(ClientController.getInstance().viewModel.getOwnNickname()));
        printToPosition(ansi().cursor(i++, 1).a("[CURRENT LOBBY]: " + (
                                ClientController.getInstance().viewModel.inLobbyOrGame() ?
                                        ClientController.getInstance().viewModel.getCurrentLobby() : //TODO: stampare UUID?
                                        "none"
                        )
                )
        );
        printToPosition(ansi().cursor(i++, 1).a("[ACTIVE LOBBIES] "));
        for (var entry : ClientController.getInstance().viewModel.getLobbies().entrySet()) {
            printToPosition(ansi().cursor(i++, 1).a(entry.getKey() + ": " + entry.getValue()));
        }
        printToPosition(ansi().cursor(i++, 1));
        printToPosition(ansi().cursor(i, 1).a(
                """
                            'createLobby <maxPlayers>' per creare una lobby,
                            'joinLobby <lobbyUUID>' per joinare una lobby esistente,
                            'setNickname <newNickname>' per cambiare il proprio nickname,
                            'leaveLobby' per lasciare la lobby in cui si e' attualmente,
                            'quit' per ritornare alla schermata del titolo
                """
                //TODO: leaveLobby andrebbe promptato solo dopo
        ));
    }

    public void updateNickname(){
        //FIXME: è così veloce che mi limiterei a richiamare lobbyScreen...
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().viewModel.getOwnNickname().length();
        lobbyScreen();
        //printToPosition(ansi().cursor(1, 11).eraseLine(Erase.FORWARD).fg(Ansi.Color.RED).bold()
        //        .a(ClientController.getInstance().ownNickname).eraseLine().reset());
        //TODO: altrimenti: erasare il nickname dalla inputLine (va fatto dopo aver implementato che non si erasa l'inputLine)
    }

    public void gameScreen() {
        clearTerminal();

        printRoundInfo();
        printStatsTable();
        showCommonPlacedCards();
        printDecks();
        printOpponentsFieldsMiniaturized();
        showField(ClientController.getInstance().viewModel.getGame().getThisPlayer());
        showHand();
        showChat();
        printStateCommandInfo();

        //FIXME: al momento comandi filtrati per stato di gioco, replicati in ogni viewState, orribile anche perchè alla GUI non servono...
        // però forse possiamo avere una lista di prompt e caricare quelli da mostrare a seconda di TUI o GUI?
        // (per esempio, trascina una carta per posizionarla)

    }

    public void printStateCommandInfo(){
        int i = 42;
        ClientGame thisGame = ClientController.getInstance().viewModel.getGame();

        Ansi printedMessage = thisGame.getCurrentPlayerIndex() != -1 ?
                ansi().cursor(i++, 2).bold().a("It is ").fg(9).a(thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex()).getNickname()).reset().bold().a("'s turn! Your available commands are: ") :
                ClientController.getInstance().viewState instanceof AwaitingReconnectionState ?
                        ansi().cursor(i++, 2).bold().a("[GAME PAUSED] Awaiting for Reconnection ...") :
                        ansi().cursor(i++, 2).bold().a("[SETUP PHASE] Every player needs to do an action! Your available commands are: ");

        printToPosition(printedMessage);
        for (var command : ClientController.getInstance().viewState.TUICommands)
            printToPosition(ansi().cursor(i++, 4).a(command));
    }

    public void printRoundInfo() {
        printToPosition(ansi().cursor(2,2).bold().a("[TURN #" +
                ClientController.getInstance().viewModel.getGame().getCurrentRound() + "]").reset());
    }

    public void printStatsTable() {
        //TODO: make a for cycle on Resource.values()?
        int i = 2;
        printToPosition(ansi().cursor(i++, 23)
                .bold().a("Points | ").reset()
                .fg(Ansi.Color.RED).a("Fungi [F]").reset()
                .a(" | ")
                .fg(Ansi.Color.GREEN).a("Plant [P]").reset()
                .a(" | ")
                .fg(Ansi.Color.BLUE).a("Animal [A]").reset()
                .a(" | ")
                .fg(Ansi.Color.MAGENTA).a("Insect [I]").reset()
                .fg(94).a(" | Scroll [S] | Ink [K] | Quill [Q]").reset()
        );

        for (ClientPlayer player : ClientController.getInstance().viewModel.getGame().getPlayers()) {
            EnumMap<Resource, Integer> playerResources = player.getOwnedResources();

            printToPosition(ansi().cursor(i, 2).a("[#" + (i - 2) + "] ").a(player.getNickname())
                    .cursor(i, 26).a(player.getPoints()) //POINTS
                    .cursor(i, 36).a(playerResources.containsKey(Resource.FUNGI) ? playerResources.get(Resource.FUNGI) : "0") //FUNGI
                    .cursor(i, 48).a(playerResources.containsKey(Resource.PLANT) ? playerResources.get(Resource.PLANT) : "0") //PLANT
                    .cursor(i, 60).a(playerResources.containsKey(Resource.ANIMAL) ? playerResources.get(Resource.ANIMAL) : "0") //ANIMAL
                    .cursor(i, 73).a(playerResources.containsKey(Resource.INSECT) ? playerResources.get(Resource.INSECT) : "0") //INSECT
                    .cursor(i, 86).a(playerResources.containsKey(Resource.SCROLL) ? playerResources.get(Resource.SCROLL) : "0") //SCROLL
                    .cursor(i, 98).a(playerResources.containsKey(Resource.INK) ? playerResources.get(Resource.INK) : "0") //INK
                    .cursor(i++, 108).a(playerResources.containsKey(Resource.QUILL) ? playerResources.get(Resource.QUILL) : "0") //QUILL
            );
        }
    }

    public void showCommonPlacedCards() {
        //erasing old placed cards
        for (int i = 12; i < 24; i++)
            printToPosition(ansi().cursor(i, 53).eraseLine(Erase.BACKWARD));

        printToPosition(ansi().cursor(8, 23).bold().a("Common placed cards: ").reset());

        //FIXME: gestire i null delle carte non presenti
        printToPosition(ansi().cursor(12, 3).a("Resource:"));
        int column = 15;
        for (var card : ClientController.getInstance().viewModel.getGame().getPlacedResources()) {
            printToPosition(ansi().cursor(10, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        column = 15;
        printToPosition(ansi().cursor(18, 3).a("Gold:"));
        for (var card : ClientController.getInstance().viewModel.getGame().getPlacedGold()) {
            printToPosition(ansi().cursor(16, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        column = 15;
        printToPosition(ansi().cursor(24, 3).a("Objective:"));
        for (var card : ClientController.getInstance().viewModel.getGame().getCommonObjectives()) {
            printToPosition(ansi().cursor(22, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        printToPosition(ansi().cursor(24, 54).a("Secret:"));
        printToPosition(ansi().cursor(22, 64).a(standardAnsi(
                ClientController.getInstance().viewModel.getGame().getOwnObjective(), Side.FRONT))
        );
    }

    public void printDecks() {
        printToPosition(ansi().cursor(8, 68).bold().a("Decks: "));

        //FIXME: in realtà bisognerebbe farsi mandare solo i back delle carte e non tutto...
        ClientCard card = ClientController.getInstance().viewModel.getGame().getTopDeckResourceCard();
        printToPosition(ansi().cursor(10, 64).a(standardAnsi(card, Side.BACK)));
        card = ClientController.getInstance().viewModel.getGame().getTopDeckGoldCard();
        printToPosition(ansi().cursor(16, 64).a(standardAnsi(card, Side.BACK)));
    }

    public void printOpponentsFieldsMiniaturized() {
        GenericPair<Integer, Integer> REDUCED_FIELD_SIZE = new GenericPair<>(11, 9);
        GenericPair<Integer, Integer> TOP_LEFT_REDUCED_FIELD = new GenericPair<>(10, 85);
        GenericPair<Integer, Integer> CENTER_REDUCED_FIELD =
                new GenericPair<>(
                        TOP_LEFT_REDUCED_FIELD.getX() + REDUCED_FIELD_SIZE.getY() / 2,
                        TOP_LEFT_REDUCED_FIELD.getY() + REDUCED_FIELD_SIZE.getX() / 2
                );
        int FIELD_SPACING = REDUCED_FIELD_SIZE.getY() + 1;
        printToPosition(ansi().cursor(8, 82).bold().a("Opponents' Fields: "));
        /*TODO: Signal to players that the miniaturized fields are truncated and if you want to see them full-sized
           you should call xxxCommand*/

        int playerIndex = 0;
        var players = ClientController.getInstance().viewModel.getCurrentLobby().getPlayers();
        players.remove(ClientController.getInstance().viewModel.getGame().getThisPlayer());
        for (Player player : players) {
            LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<ClientCard, Side>> field =
                    ((ClientPlayer) player).getPlacedCards();

            for (var entry : field.sequencedEntrySet().stream()
                    .filter( (entry) ->
                            Math.abs(entry.getKey().getX()) <= REDUCED_FIELD_SIZE.getX()/2 &&
                            Math.abs(entry.getKey().getY()) <= REDUCED_FIELD_SIZE.getY()/2)
                    .toList()) {
                printToPosition(ansi().cursor(
                                (playerIndex * FIELD_SPACING) + CENTER_REDUCED_FIELD.getX() - entry.getKey().getY(),
                                CENTER_REDUCED_FIELD.getY() + entry.getKey().getX())
                        .bg(entry.getValue().getX().TUI_SPRITES.get(Side.BACK).get(2).getFirst().getY()[1]).a(" ")
                );
            }
            playerIndex++;
        }
    }

    //TODO: controllare che sia fixata la lunghezza massima del messaggio (70+80 = 150 caratteri vanno bene?)
    public void showChat() {
        List<String> chatLog = ClientController.getInstance().viewModel.getGame().getChatLog();
        printToPosition(ansi().cursor(2, 120).bold().a("Last chat messages: ").reset());
        for (int i = 0; i < 3; i++)
            printToPosition(ansi().cursor(3 + i, 122).eraseLine(Erase.FORWARD)
                    .a((chatLog.size() >= 3 - i) ? chatLog.get(chatLog.size() - 3 + i) : "")
            );
    }

    public Ansi standardAnsi(ClientCard card, Side side) {
        if (card == null) return Ansi.ansi();
        if (card.ID == -1) return Ansi.ansi();

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
        if (card.ID == -1) return Ansi.ansi();

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
        //erasing field area
        int fieldStartingColumn = FIELD_TOP_LEFT.getY();
        for (int i = FIELD_TOP_LEFT.getX(); i < FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX(); i++)
            printToPosition(ansi().cursor(i, fieldStartingColumn).eraseLine(Erase.FORWARD));

        ClientCard card = ClientController.getInstance().viewModel.getGame().getCardsInHand().getFirst();
        printToPosition(ansi().cursor(15, 110).bold().eraseLine(Erase.FORWARD)
                .a("Choose which side you want to play your assigned initial card on: ").reset());
        printToPosition(ansi().cursor(20, 110).a(upscaledAnsi(card, Side.FRONT)));
        printToPosition(ansi().cursor(20, 170).a(upscaledAnsi(card, Side.BACK)));
    }

    @Override
    public void showObjectiveCardsChoice() {
        //erasing field area
        int fieldStartingColumn = FIELD_TOP_LEFT.getY();
        for (int i = FIELD_TOP_LEFT.getX(); i < FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX(); i++)
            printToPosition(ansi().cursor(i, fieldStartingColumn).eraseLine(Erase.FORWARD));

        printToPosition(ansi().cursor(15, 110).bold().eraseLine(Erase.FORWARD)
                .a("Choose which card you want to keep as your secret objective: ").reset());
        if (!((ChooseObjectiveCardsState) ClientController.getInstance().viewState).objectivesSelection.isEmpty()) {
            ClientCard card = ((ChooseObjectiveCardsState) ClientController.getInstance().viewState).objectivesSelection.getFirst();
            printToPosition(ansi().cursor(20, 110).a(upscaledAnsi(card, Side.FRONT)));
            card = ((ChooseObjectiveCardsState) ClientController.getInstance().viewState).objectivesSelection.get(1);
            printToPosition(ansi().cursor(20, 170).a(upscaledAnsi(card, Side.FRONT)));
        }
    }

    private GenericPair<Integer, Integer> findExtremeCoordinates(ClientPlayer target, ToIntBiFunction<Integer, Integer> criterion) {
        return target.getPlacedCards().keySet().stream()
                .reduce(new GenericPair<>(0, 0),
                        (a, b) -> new GenericPair<>(
                                criterion.applyAsInt(a.getX(), b.getX()),
                                criterion.applyAsInt(a.getY(), b.getY())
                        )
                );
    }

    @Override
    public void showField(ClientPlayer player) {
        //erasing field area
        int fieldStartingColumn = FIELD_TOP_LEFT.getY();
        for (int i = FIELD_TOP_LEFT.getX(); i < FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX(); i++)
            printToPosition(ansi().cursor(i, fieldStartingColumn).eraseLine(Erase.FORWARD));

        printToPosition(ansi().cursor(FIELD_TOP_LEFT.getX() - 2, FIELD_TOP_LEFT.getY()).a("Field of: ")
                .bold().a(player.getNickname()).eraseLine(Erase.FORWARD));

        GenericPair<Integer, Integer> maxCoordinates = findExtremeCoordinates(player, Math::max);
        GenericPair<Integer, Integer> minCoordinates = findExtremeCoordinates(player, Math::min);

        final GenericPair<Float, Float> fieldCenterOfGravity =
                new GenericPair<>(
                        ((float) minCoordinates.getY() + maxCoordinates.getY()) / 2,
                        ((float) minCoordinates.getX() + maxCoordinates.getX()) / 2
                );

        final GenericPair<Integer, Integer> initialCardCenter = new GenericPair<>(
                FIELD_CENTER.getX() + (int) (fieldCenterOfGravity.getX() * CURSOR_OFFSET.getX()),
                FIELD_CENTER.getY() - (int) (fieldCenterOfGravity.getY() * CURSOR_OFFSET.getY())
        );

        final GenericPair<Integer, Integer> initialCardPosition = new GenericPair<>(
                initialCardCenter.getX() - CARD_SIZE.getY()/2,
                initialCardCenter.getY() - CARD_SIZE.getX()/2
        );

        player.getPlacedCards().sequencedEntrySet()
                .forEach((entry) -> printToPosition(ansi().cursor(
                                initialCardPosition.getX() - (entry.getKey().getY() * CURSOR_OFFSET.getX()),
                                initialCardPosition.getY() + (entry.getKey().getX() * CURSOR_OFFSET.getY())
                                ).a(standardAnsi(entry.getValue().getX(), entry.getValue().getY()))
                        )
                );

        player.getOpenCorners()
                .forEach((corner) -> printToPosition(ansi().cursor(
                                                initialCardPosition.getX() - (corner.getY() * CURSOR_OFFSET.getX()) + 2,
                                                initialCardPosition.getY() + (corner.getX() * CURSOR_OFFSET.getY()) + 5
                                        )
                                        .cursorMove(-String.valueOf(corner.getX()).length(), 0)
                                        //.a(AnsiRenderer.renderCodes("faint", "blink_slow")) FIXME: perchè non funziona?
                                        .a("[" + corner.getX() + "," + corner.getY() + "]")
                        )
                );
    }

    public void showLeaderboard(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
        int FIRST_ROW = 15;
        int ROW_OFFSET = 2;
        int index = 1;

        clearTerminal();

        //LENGTH: 70
        printToPosition(ansi().cursor(5,72).fg(Resource.QUILL.ANSI_COLOR).a(" _      _____  ___ ______ _________________  _____  ___  ____________").reset());
        printToPosition(ansi().cursor(6,72).fg(Resource.FUNGI.ANSI_COLOR).a("| |    |  ___|/ _ \\|  _  \\  ___| ___ \\ ___ \\|  _  |/ _ \\ | ___ \\  _  \\").reset());
        printToPosition(ansi().cursor(7,72).fg(Resource.ANIMAL.ANSI_COLOR).a("| |    | |__ / /_\\ \\ | | | |__ | |_/ / |_/ /| | | / /_\\ \\| |_/ / | | |").reset());
        printToPosition(ansi().cursor(8,72).fg(Resource.PLANT.ANSI_COLOR).a("| |    |  __||  _  | | | |  __||    /| ___ \\| | | |  _  ||    /| | | |").reset());
        printToPosition(ansi().cursor(9, 72).fg(Resource.INSECT.ANSI_COLOR).a("| |____| |___| | | | |/ /| |___| |\\ \\| |_/ /\\.\\_/ / | | || |\\ \\| |/ /").reset());
        printToPosition(ansi().cursor(10,72).fg(Resource.QUILL.ANSI_COLOR).a("\\_____/\\____/\\_| |_/___/ \\____/\\_| \\_\\____/  \\___/\\_| |_/\\_| \\_|___/").reset());

        printToPosition(ansi()
                .cursor(FIRST_ROW, 72).a("Nickname")
                .cursor(FIRST_ROW, 92).a("Total Points")
                .cursor(FIRST_ROW,112).a("Points from Objective Cards")
        );

        for(var row: leaderboard){
            printToPosition(ansi()
                    .cursor(FIRST_ROW+(index*ROW_OFFSET),62).a("[#" + index + "]")
                    .cursor(FIRST_ROW+(index*ROW_OFFSET),72).a(row.getX())
                    .cursor(FIRST_ROW+(index*ROW_OFFSET),95).a(row.getY() != -1 ? row.getY() + " pt." : "NaN")
                    .cursor(FIRST_ROW+(index*ROW_OFFSET),124).a(row.getZ() != -1 ? row.getZ() + " pt.": "NaN")
            );
            index++;
        }

        if(gameEndedDueToDisconnections)
            printToPosition(ansi()
                    .cursor(30,72).a("Since the game ended due to disconnections of all the other players, ")
                    .bold().fg(9).a(leaderboard.getFirst().getX()).reset().a(" is the WINNER!")
            );

        System.out.print(ansi().cursor(TUIListener.COMMAND_INPUT_ROW - 2, 1).a("Type 'ok' to return to lobbies")
                .cursorDownLine()
                .a("------------------------------------------------------------------").eraseLine(Erase.FORWARD)
                .cursorDownLine()
                .a("> [" + ClientController.getInstance().viewModel.getOwnNickname() + "] "));
    }

    @Override
    public void showHand() {
        //FIXME: erase or overwrite old hand/cards? (is it needed or you always have at least 3 cards in hand?)

        int column = 10;
        printToPosition(ansi().cursor(28, 2).bold().a("Your hand: ").reset());
        printToPosition(ansi().cursor(32, 3).a("Front:"));
        printToPosition(ansi().cursor(38, 3).a("Back:"));
        for (var card : ClientController.getInstance().viewModel.getGame().getCardsInHand()) {
            printToPosition(ansi().cursor(30, column).a(standardAnsi(card, Side.FRONT)));
            printToPosition(ansi().cursor(36, column).a(standardAnsi(card, Side.BACK)));
            column += 20;
        }
    }
}