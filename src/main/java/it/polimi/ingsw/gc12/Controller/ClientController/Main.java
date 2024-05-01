package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIView;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.TitleScreenState;
import it.polimi.ingsw.gc12.HelloApplication;
import javafx.application.Application;
import org.fusesource.jansi.AnsiConsole;

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
                AnsiConsole.systemInstall();
                ClientController.getInstance().view = TUIView.getInstance();
                ClientController.getInstance().viewState = new TitleScreenState();
                ClientController.getInstance().viewState.executeState();
            } else if (graphics.equals("gui")) {
                //FIXME: want to get controller here...
                new Thread(() -> Application.launch(HelloApplication.class, args)).start();
            }
        } while (!graphics.equals("tui") && !graphics.equals("gui"));
    }
}
