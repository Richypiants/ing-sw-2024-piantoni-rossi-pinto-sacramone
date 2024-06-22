package it.polimi.ingsw.gc12.Main.Client;

import it.polimi.ingsw.gc12.View.Client.GUI.GUIViews.GUIView;
import it.polimi.ingsw.gc12.View.Client.TUI.TUIViews.TUIView;
import it.polimi.ingsw.gc12.View.Client.ViewStates.TitleScreenState;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

import java.util.Scanner;

/**
 * Entry point for the client application. Allows the user to choose between TUI (Text User Interface) and GUI (Graphical User Interface)
 */
 public class ClientMain {

    /**
     * Main method that starts the client application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String graphics;

        // Prompt user to select application graphics (TUI or GUI)
        do {
            System.out.println("Select the desired application graphics (TUI - GUI): ");
            graphics = scanner.nextLine().trim().toLowerCase();
            if (graphics.equals("tui"))
                ViewState.setView(TUIView.getInstance());
            else if (graphics.equals("gui"))
                ViewState.setView(GUIView.getInstance());
        } while (!graphics.equals("tui") && !graphics.equals("gui"));

        TitleScreenState newState = new TitleScreenState();
        ViewState.setCurrentState(newState);
        newState.executeState();
    }
}
