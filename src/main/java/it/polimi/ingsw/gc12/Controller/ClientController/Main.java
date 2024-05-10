package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIApplication;
import it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIView;
import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIView;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.TitleScreenState;
import javafx.application.Application;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //FIXME: perchè System.console() dava null?
        Scanner scanner = new Scanner(System.in);
        String graphics = "";
        do {
            System.out.println("Select the desired application graphics (TUI - GUI): ");
            graphics = scanner.nextLine().toLowerCase();
            if (graphics.equals("tui")) {
                ClientController.getInstance().view = TUIView.getInstance();
                ClientController.getInstance().viewState = new TitleScreenState();
                ClientController.getInstance().viewState.executeState();

                /*ClientController.getInstance().view = TUIView.getInstance();

                ArrayList<Triplet<String, Integer, Integer>> POINT_STATS = new ArrayList<>();
                POINT_STATS.add(new Triplet<>("TheSpecia", 100, 10));
                POINT_STATS.add(new Triplet<>("Piants", 10, 1));
                POINT_STATS.add(new Triplet<>("$acramoney", 9, 6));

                ClientController.getInstance().viewState = new LeaderboardScreenState(POINT_STATS);
                ClientController.getInstance().viewState.executeState();
                */
            } else if (graphics.equals("gui")) {
                ClientController.getInstance().view = GUIView.getInstance();
                new Thread(() -> Application.launch(GUIApplication.class, args)).start();
            }
        } while (!graphics.equals("tui") && !graphics.equals("gui"));
    }
}
