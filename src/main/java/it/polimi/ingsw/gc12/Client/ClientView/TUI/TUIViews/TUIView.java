package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIParser;
import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

public class TUIView extends View {

    private static TUIView SINGLETON_TUI_INSTANCE = null;
    public static TUIParser listener;
    public static final Console console = System.console();

    private final GenericPair<Integer, Integer> TERMINAL_SIZE = new GenericPair<>(49, 211); //x: rows, y:columns

    public TUIView() {}

    public static TUIView getInstance() {
        if (SINGLETON_TUI_INSTANCE == null) {
            SINGLETON_TUI_INSTANCE = new TUIView();
            AnsiConsole.systemInstall();
            listener = TUIParser.getInstance();
            try {
                //FIXME: on Mac bash instead of cmd (on Linux too?)
                new ProcessBuilder("cmd", "/c", "mode con:cols=" + SINGLETON_TUI_INSTANCE.TERMINAL_SIZE.getY() + " lines=" + SINGLETON_TUI_INSTANCE.TERMINAL_SIZE.getX())
                        .inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return SINGLETON_TUI_INSTANCE;
    }

    public static void clearTerminal() {
        System.out.print(ansi()
                .saveCursorPosition()
                .cursor(TUIParser.COMMAND_INPUT_ROW - 2, 1)
                .eraseScreen(Ansi.Erase.BACKWARD).eraseLine(Ansi.Erase.FORWARD)
                .cursorDownLine()
                .a("------------------------------------------------------------------").eraseLine(Ansi.Erase.FORWARD)
                .cursorDownLine()
                .a("> [" + ClientController.getInstance().VIEWMODEL.getOwnNickname() + "] ")
                .restoreCursorPosition()
                .eraseScreen(Ansi.Erase.FORWARD)
        );
    }

    @Override
    public void printError(Throwable error) {
        System.out.print(ansi().saveCursorPosition()
                .cursor(TUIParser.EXCEPTIONS_ROW, 1)
                .a(error.getMessage()).reset()
                .restoreCursorPosition()
        );
        //FIXME: autoResetting... should keep it?
    }

    public void printToPosition(Ansi toPrint) {
        System.out.print(ansi().saveCursorPosition()
                .a(toPrint).reset()
                .restoreCursorPosition()
                .eraseScreen(Ansi.Erase.FORWARD)
        );
        //FIXME: autoResetting... should keep it?
    }

    @Override
    public void titleScreen() {
        TUITitleView.getInstance().titleScreen();
    }

    @Override
    public void connectionSetupScreen() {
        TUIConnectionView.getInstance().connectionSetupScreen();
    }

    @Override
    public void connectedConfirmation() {
        TUIConnectionView.getInstance().connectedConfirmation();
    }

    @Override
    public void quittingScreen() {
        clearTerminal();
        printToPosition(ansi().cursor(1, 1).a("Returning to title screen..."));
    }

    @Override
    public void lobbiesScreen(){
        TUILobbiesView.getInstance().lobbiesScreen();
    }

    @Override
    public void showNickname() {
        TUILobbiesView.getInstance().showNickname();
    }

    @Override
    public void gameScreen() {
        TUIGameView.getInstance().gameScreen();
    }

    @Override
    public void awaitingScreen() {
        TUIGameView.getInstance().awaitingScreen();
    }

    @Override
    public void showChat() {
        TUIGameView.getInstance().showChat();
    }

    @Override
    public void showInitialCardsChoice(){
        TUIGameView.getInstance().showInitialCardsChoice();
    }

    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        TUIGameView.getInstance().showObjectiveCardsChoice(objectivesSelection);
    }

    @Override
    public void showCommonPlacedCards(){
        TUIGameView.getInstance().showCommonPlacedCards();
    }

    @Override
    public void showLeaderboard(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections) {
        TUIGameView.getInstance().showLeaderboard(POINTS_STATS, gameEndedDueToDisconnections);
    }

    @Override
    public void showField(ClientPlayer player) {
        TUIGameView.getInstance().showField(player);
    }

    @Override
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        TUIGameView.getInstance().moveField(centerOffset);
    }

    @Override
    public void showHand() {
        TUIGameView.getInstance().showHand();
    }
}
