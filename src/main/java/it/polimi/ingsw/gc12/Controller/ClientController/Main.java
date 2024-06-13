package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews.GUIView;
import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews.TUIView;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.TitleScreenState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String graphics;

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
