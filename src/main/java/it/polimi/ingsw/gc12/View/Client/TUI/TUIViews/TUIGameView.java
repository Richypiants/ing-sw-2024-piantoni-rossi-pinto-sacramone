package it.polimi.ingsw.gc12.View.Client.TUI.TUIViews;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.View.Client.TUI.TUIParser;
import it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.ToIntBiFunction;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * The TUIGameView class represents the Textual User Interface (TUI) view for displaying the game
 * and all the details composing the game board.
 * <p>
 * This class handles the rendering of various game elements including player stats, cards, decks, opponent fields,
 * and the player's own field. It utilizes the Ansi library for terminal control and color rendering.
 */
public class TUIGameView extends TUIView{

    /**
     * Singleton instance of the TUIGameView class.
     */
    private static TUIGameView gameView = null;

    /**
     * Dimensions of the field where the game is displayed (height, width).
     */
    private final GenericPair<Integer, Integer> FIELD_SIZE = new GenericPair<>(30, 105); //x: height, y: width

    /**
     * Top-left position of the game field (startingRow, startingColumn).
     */
    private final GenericPair<Integer, Integer> FIELD_TOP_LEFT = new GenericPair<>(10, 105); //x: startingRow, y: startingColumn

    /**
     * Center of the game field, computed from FIELD_TOP_LEFT and FIELD_SIZE.
     */
    private final GenericPair<Integer, Integer> FIELD_CENTER = new GenericPair<>(
            FIELD_TOP_LEFT.getX() + (FIELD_SIZE.getX() / 2),
            FIELD_TOP_LEFT.getY() + (FIELD_SIZE.getY() / 2)
    );

    /**
     * Size of a card in rows and columns.
     */
    private final GenericPair<Integer, Integer> CARD_SIZE = new GenericPair<>(13, 5);

    /**
     * Offset used for cursor positioning while printing multiple cards, manually computed.
     */
    private final GenericPair<Integer, Integer> CURSOR_OFFSET = new GenericPair<>(3, 11);

    /**
     * Dynamic center of the game field, adjusted during field movement.
     */
    private GenericPair<Integer, Integer> dynamicFieldCenter = new GenericPair<>(0, 0);

    /**
     * Index of the currently displayed player's field.
     */
    private int currentShownPlayerIndex = -1;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private TUIGameView() {
        super();
    }

    /**
     * Retrieves the singleton instance of TUIGameView.
     *
     * @return Singleton instance of TUIGameView.
     */
    public static TUIGameView getInstance() {
        if (gameView == null) {
            gameView = new TUIGameView();
        }
        return gameView;
    }

    /**
     * Renders the TUI sprite of the standard size for the given ClientCard on the specified Side.
     * It is also able to render null cards.
     *
     * @param card The card to be rendered in Ansi characters.
     * @param side The side to be rendered.
     * @return The Ansi object containing the formatting information to be printed by the AnsiConsole.
     */
    private Ansi standardAnsi(ClientCard card, Side side) {
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

    /**
     * Renders the TUI sprite of the upscaled (bigger) size for the given ClientCard on the specified Side.
     * It is also able to render null cards.
     *
     * @param card The card to be rendered in Ansi characters.
     * @param side The side to be rendered.
     * @return The Ansi object containing the formatting information to be printed by the AnsiConsole.
     */
    private Ansi upscaledAnsi(ClientCard card, Side side) {
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

    /**
     * Prints all the game elements composing the game board and illustrating the information related to the game.
     */
    @Override
    public void gameScreen() {
        clearTerminal();

        printRoundInfo();
        printStatsTable();
        showCommonPlacedCards();
        printDecks();
        printOpponentsFieldsMiniaturized();
        showField(VIEWMODEL.getCurrentGame().getThisPlayer());
        showHand();
        updateChat();
        printStateCommandInfo();
    }

    /**
     * Refreshes the screen to print the message that the game is currently paused due to disconnections.
     */
    @Override
    public void awaitingScreen() {
        gameScreen();
    }

    /**
     * Renders the current state and available command information at the bottom of the screen.
     */
    private void printStateCommandInfo() {
        ClientGame thisGame = VIEWMODEL.getCurrentGame();

        Ansi printedMessage = thisGame.getCurrentPlayerIndex() != -1 ?
                ansi().cursor(42, 2).bold()
                        .a(
                                (thisGame.getTurnsLeftUntilGameEnds() == -1 ? "" : "[" + thisGame.getTurnsLeftUntilGameEnds() + " TURNS LEFT] ") +
                                        "It is "
                        ).fg(9).a(thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex()).getNickname())
                        .reset().bold().a("'s turn! Your available commands are: ") :
                ViewState.getCurrentState() instanceof AwaitingReconnectionState ?
                        ansi().cursor(42, 2).bold().a("[GAME PAUSED] Awaiting for reconnection of other players...") :
                        ansi().cursor(42, 2).bold().a("[SETUP PHASE] Every player needs to do an action! Your available commands are: ");

        printToPosition(printedMessage);

        int startingRowMessage = 43;
        int availableLinesForCommands = 4;
        int printedMessages = 0;
        for (var command : ViewState.getCurrentState().TUICommands) {
            int actualColumn = (printedMessages >= availableLinesForCommands ? 120 : 4);
            int actualRow = startingRowMessage + printedMessages % availableLinesForCommands;
            printToPosition(ansi().cursor(actualRow, actualColumn).a(command));
            printedMessages++;
        }
    }

    /**
     * Prints the current round information at the top of the screen.
     */
    private void printRoundInfo() {
        printToPosition(ansi().cursor(2, 2).bold().a("[ROUND #" +
                VIEWMODEL.getCurrentGame().getCurrentRound() + "]").reset());
    }

    /**
     * Prints the player statistics table at the top of the screen.
     */
    private void printStatsTable() {
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

        for (ClientPlayer player : VIEWMODEL.getCurrentGame().getPlayers()) {
            EnumMap<Resource, Integer> playerResources = player.getOwnedResources();
            Ansi playerNickname = ansi().fg(Ansi.Color.valueOf(player.getColor().name())).a(player.getNickname());
            if (!player.isActive())
                playerNickname.a(" [X]");

            printToPosition(ansi().cursor(i, 2).a("[#" + (i - 2) + "] ").a(playerNickname).reset()
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

    /**
     * Prints the common placed cards (resources, gold, objectives) on the left side of the screen.
     */
    @Override
    public void showCommonPlacedCards() {
        //Erasing old placed cards
        for (int i = 12; i < 24; i++)
            printToPosition(ansi().cursor(i, 53).eraseLine(Ansi.Erase.BACKWARD));

        printToPosition(ansi().cursor(8, 23).bold().a("Common placed cards: ").reset());

        printToPosition(ansi().cursor(12, 3).a("Resource:"));
        int column = 15;
        for (var card : VIEWMODEL.getCurrentGame().getPlacedResources()) {
            printToPosition(ansi().cursor(10, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        column = 15;
        printToPosition(ansi().cursor(18, 3).a("Gold:"));
        for (var card : VIEWMODEL.getCurrentGame().getPlacedGolds()) {
            printToPosition(ansi().cursor(16, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        column = 15;
        printToPosition(ansi().cursor(24, 3).a("Objective:"));
        for (var card : VIEWMODEL.getCurrentGame().getCommonObjectives()) {
            printToPosition(ansi().cursor(22, column).a(standardAnsi(card, Side.FRONT)));
            column += 20;
        }

        printToPosition(ansi().cursor(24, 54).a("Secret:"));
        printToPosition(ansi().cursor(22, 64).a(
                standardAnsi(VIEWMODEL.getCurrentGame().getOwnObjective(), Side.FRONT)
        ));
    }

    /**
     * Prints the decks (resource and gold) on the center left side of the screen.
     */
    private void printDecks() {
        printToPosition(ansi().cursor(8, 68).bold().a("Decks: "));

        ClientCard card = VIEWMODEL.getCurrentGame().getTopDeckResourceCard();
        printToPosition(ansi().cursor(10, 64).a(standardAnsi(card, Side.BACK)));
        card = VIEWMODEL.getCurrentGame().getTopDeckGoldCard();
        printToPosition(ansi().cursor(16, 64).a(standardAnsi(card, Side.BACK)));
    }

    /**
     * Prints truncated miniaturized views of opponents' fields on the right side of the screen.
     */
    private void printOpponentsFieldsMiniaturized() {
        GenericPair<Integer, Integer> REDUCED_FIELD_SIZE = new GenericPair<>(11, 9);
        GenericPair<Integer, Integer> TOP_LEFT_REDUCED_FIELD = new GenericPair<>(10, 85);
        GenericPair<Integer, Integer> CENTER_REDUCED_FIELD =
                new GenericPair<>(
                        TOP_LEFT_REDUCED_FIELD.getX() + REDUCED_FIELD_SIZE.getY() / 2,
                        TOP_LEFT_REDUCED_FIELD.getY() + REDUCED_FIELD_SIZE.getX() / 2
                );
        int FIELD_SPACING = REDUCED_FIELD_SIZE.getY() + 1;
        int INITIAL_NICKNAME_POSITION = 9;

        printToPosition(ansi().cursor(8, 82).bold().a("Opponents' Fields: "));

        int playerIndex = 0;
        ArrayList<ClientPlayer> players = VIEWMODEL.getCurrentGame().getPlayers();
        players.remove(VIEWMODEL.getCurrentGame().getThisPlayer());
        for (var player : players) {
            printToPosition(ansi().cursor(
                    INITIAL_NICKNAME_POSITION + (playerIndex * (REDUCED_FIELD_SIZE.getY() + 2)),
                    82).fg(Ansi.Color.valueOf(player.getColor().name())).a(player.getNickname()).reset());

            LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<ClientCard, Side>> field =
                    (player).getPlacedCards();

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

    /**
     * Prints the current chat log messages in the top-right corner of the screen.
     */
    @Override
    public void updateChat() {
        List<String> chatLog = VIEWMODEL.getCurrentGame().getChatLog();
        printToPosition(ansi().cursor(2, 120).bold().a("Last chat messages: ").reset());
        for (int i = 0; i < 3; i++)
            printToPosition(ansi().cursor(3 + i, 122).eraseLine(Ansi.Erase.FORWARD)
                    .a((chatLog.size() >= 3 - i) ? chatLog.get(chatLog.size() - 3 + i) : "")
            );
    }

    /**
     * Clears the terminal area corresponding to the initial cards choice section and displays
     * the initial card side selection for the player to choose from.
     */
    @Override
    public void showInitialCardsChoice() {
        //Erasing field area
        int fieldStartingColumn = FIELD_TOP_LEFT.getY();
        for (int i = FIELD_TOP_LEFT.getX(); i < FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX(); i++)
            printToPosition(ansi().cursor(i, fieldStartingColumn).eraseLine(Ansi.Erase.FORWARD));

        ClientCard card = VIEWMODEL.getCurrentGame().getCardsInHand().getFirst();
        printToPosition(ansi().cursor(15, 110).bold().eraseLine(Ansi.Erase.FORWARD)
                .a("Choose which side you want to play your assigned initial card on: ").reset());
        printToPosition(ansi().cursor(20, 110).a(upscaledAnsi(card, Side.FRONT)));
        printToPosition(ansi().cursor(20, 170).a(upscaledAnsi(card, Side.BACK)));
    }

    /**
     * Clears the terminal area corresponding to the objective cards choice section and displays
     * the available secret objective card selection for the player to choose as their secret objective.
     *
     * @param objectivesSelection The list of objective cards available for selection.
     */
    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        //Erasing field area
        int fieldStartingColumn = FIELD_TOP_LEFT.getY();
        for (int i = FIELD_TOP_LEFT.getX(); i < FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX(); i++)
            printToPosition(ansi().cursor(i, fieldStartingColumn).eraseLine(Ansi.Erase.FORWARD));

        printToPosition(ansi().cursor(15, 110).bold().eraseLine(Ansi.Erase.FORWARD)
                .a("Choose which card you want to keep as your secret objective: ").reset());
        if (!objectivesSelection.isEmpty()) {
            ClientCard card = objectivesSelection.getFirst();
            printToPosition(ansi().cursor(20, 110).a(upscaledAnsi(card, Side.FRONT)));
            card = objectivesSelection.get(1);
            printToPosition(ansi().cursor(20, 170).a(upscaledAnsi(card, Side.FRONT)));
        }
    }

    /**
     * Finds extreme coordinates (min or max)over the cartesian filed of cards placed on a player's field.
     *
     * @param target   The player whose field is being examined.
     * @param criterion Function to determine whether to find min or max coordinates.
     * @return Extreme coordinates based on the given criterion.
     */
    private GenericPair<Integer, Integer> findExtremeCoordinates(ClientPlayer target, ToIntBiFunction<Integer, Integer> criterion) {
        return target.getPlacedCards().keySet().stream()
                .reduce(new GenericPair<>(0, 0),
                        (a, b) -> new GenericPair<>(
                                criterion.applyAsInt(a.getX(), b.getX()),
                                criterion.applyAsInt(a.getY(), b.getY())
                        )
                );
    }

    /**
     * Displays the field of the specified player on the terminal.
     *
     * @param player The player whose field is to be displayed.
     */
    @Override
    public void showField(ClientPlayer player) {
        dynamicFieldCenter = new GenericPair<>(0, 0);
        currentShownPlayerIndex = VIEWMODEL.getCurrentGame().getPlayers().indexOf(player);
        moveField(new GenericPair<>(0, 0));
    }

    /**
     * Moves and displays the field currently displayed by x cards left and y cards down.
     * <p>
     * This method updates the dynamic field center based on the provided offset
     * redrawing the player's field including placed cards
     * and open corners. The field is centered around the calculated field center of gravity.
     * </p>
     *
     * @param dynamicFieldCenterOffset The offset to adjust the field view.
     */
    @Override
    public void moveField(GenericPair<Integer, Integer> dynamicFieldCenterOffset) {
        dynamicFieldCenter = new GenericPair<>(
                dynamicFieldCenter.getX() + dynamicFieldCenterOffset.getX(),
                dynamicFieldCenter.getY() + dynamicFieldCenterOffset.getY()
        );

        ClientPlayer player = VIEWMODEL.getCurrentGame().getPlayers().get(currentShownPlayerIndex);

        //Erasing field area
        int fieldStartingColumn = FIELD_TOP_LEFT.getY();
        for (int i = FIELD_TOP_LEFT.getX(); i < FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX(); i++)
            printToPosition(ansi().cursor(i, fieldStartingColumn).eraseLine(Ansi.Erase.FORWARD));

        printToPosition(ansi().cursor(FIELD_TOP_LEFT.getX() - 2, FIELD_TOP_LEFT.getY()).a("Field of: ")
                .bold().a(player.getNickname()).eraseLine(Ansi.Erase.FORWARD));

        GenericPair<Integer, Integer> maxCoordinates = findExtremeCoordinates(player, Math::max);
        GenericPair<Integer, Integer> minCoordinates = findExtremeCoordinates(player, Math::min);

        final GenericPair<Float, Float> fieldCenterOfGravity =
                new GenericPair<>(
                        ((float) minCoordinates.getY() + maxCoordinates.getY()) / 2 + dynamicFieldCenter.getY(),
                        ((float) minCoordinates.getX() + maxCoordinates.getX()) / 2 + dynamicFieldCenter.getX()
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
                .forEach((entry) -> {
                            int printRow = initialCardPosition.getX() - (entry.getKey().getY() * CURSOR_OFFSET.getX());
                            int printColumn = initialCardPosition.getY() + (entry.getKey().getX() * CURSOR_OFFSET.getY());
                    checkCardOutOfFieldBorder(printRow, printColumn);

                    if (!checkCardOutOfFieldBorder(printRow, printColumn))
                                printToPosition(ansi().cursor(printRow, printColumn)
                                        .a(standardAnsi(entry.getValue().getX(), entry.getValue().getY()))
                                );
                        }
                );

        player.getOpenCorners()
                .forEach((corner) -> {
                            int printRow = initialCardPosition.getX() - (corner.getY() * CURSOR_OFFSET.getX()) + 2;
                            int printColumn = initialCardPosition.getY() + (corner.getX() * CURSOR_OFFSET.getY()) + 5;
                    checkCardOutOfFieldBorder(printRow, printColumn);

                    if (!checkCardOutOfFieldBorder(printRow, printColumn))
                                printToPosition(ansi().cursor(printRow, printColumn)
                                        .cursorMove(-String.valueOf(corner.getX()).length(), 0)
                                        .a("[" + corner.getX() + "," + corner.getY() + "]")
                                );
                        }
                );
    }

    /**
     * Checks if a card would be printed out of the visible field area.
     *
     * @param printRow    Row position to check.
     * @param printColumn Column position to check.
     * @return True if the card is out of bounds, false otherwise.
     */
    private boolean checkCardOutOfFieldBorder(int printRow, int printColumn) {
        boolean outOfBounds = false;

        if (printRow < FIELD_TOP_LEFT.getX() + 2) {
            printToPosition(ansi().cursor(FIELD_TOP_LEFT.getX()-1, FIELD_CENTER.getY())
                    .a("^").cursorMove(-1, 1).a("|"));
            outOfBounds = true;
        }

        if (printColumn < FIELD_TOP_LEFT.getY() + 2) {
            printToPosition(ansi().cursor(FIELD_CENTER.getX(), FIELD_TOP_LEFT.getY()-1).a("<-"));
            outOfBounds = true;
        }

        if (printRow > FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX() - 1 - CURSOR_OFFSET.getX() - 1 - 2) {
            printToPosition(ansi().cursor(
                    FIELD_TOP_LEFT.getX() + FIELD_SIZE.getX() - 1,
                    FIELD_CENTER.getY()
            ).a("|").cursorMove(-1, 1).a("v"));
            outOfBounds = true;
        }

        if (printColumn > FIELD_TOP_LEFT.getY() + FIELD_SIZE.getY() - 1 - CURSOR_OFFSET.getY() - 1 - 2) {
            printToPosition(ansi().cursor(
                            FIELD_CENTER.getX(),
                            FIELD_TOP_LEFT.getY() + FIELD_SIZE.getY() - 1)
                    .a("->"));
            outOfBounds = true;
        }

        return outOfBounds;
    }

    /**
     * Prints the leaderboard at the end of the game, showing player rankings and points.
     *
     * @param leaderboard List of triplets containing player names, scores, and ranks.
     * @param gameEndedDueToDisconnections True if the game ended due to disconnections.
     */
    @Override
    public void leaderboardScreen(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
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
                    .cursor(FIRST_ROW+(index*ROW_OFFSET),95).a(row.getY() != -1 ? row.getY() + " pt." : "N/A")
                    .cursor(FIRST_ROW+(index*ROW_OFFSET),124).a(row.getZ() != -1 ? row.getZ() + " pt.": "N/A")
            );
            index++;
        }

        Ansi toPrint = ansi().bold().fg(9).a(leaderboard.getFirst().getX()).reset().a(" is the WINNER!");

        if(gameEndedDueToDisconnections)
            toPrint = ansi().a("Since the game ended due to disconnections of all the other players, ").a(toPrint);

        printToPosition(ansi().cursor(30, 72).a(toPrint));

        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW - 2, 1).a("Type 'OK' to return to lobbies")
                .cursorDownLine()
                .a("------------------------------------------------------------------").eraseLine(Ansi.Erase.FORWARD)
                .cursorDownLine()
                .a("> [" + VIEWMODEL.getOwnNickname() + "] "));
    }

    /**
     * Prints the player's hand (front and back sides) in the center left part of the screen.
     */
    @Override
    public void showHand() {
        int column = 10;
        printToPosition(ansi().cursor(28, 2).bold().a("Your hand: ").reset());
        printToPosition(ansi().cursor(32, 3).a("Front:"));
        printToPosition(ansi().cursor(38, 3).a("Back:"));
        for (var card : VIEWMODEL.getCurrentGame().getCardsInHand()) {
            printToPosition(ansi().cursor(30, column).a(standardAnsi(card, Side.FRONT)));
            printToPosition(ansi().cursor(36, column).a(standardAnsi(card, Side.BACK)));
            column += 20;
        }
    }
}
