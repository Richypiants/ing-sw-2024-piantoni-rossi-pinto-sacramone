package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIView;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.TitleScreenState;
import org.fusesource.jansi.AnsiConsole;

public class Main {

    public static void main(String[] args) {
        ClientController.getInstance().view = TUIView.getInstance();
        ClientController.getInstance().viewState = new TitleScreenState();
        ClientController.getInstance().viewState.transition();
        //TODO: add TUI to args
        //if (args[1].equalsIgnoreCase("TUI"))
            //view = new TUIView().run();
        /*else
            view = new GUIView().run();
         */

        AnsiConsole.systemInstall();
        //System.out.println(ansi().fg(Ansi.Color.GREEN).a("Hello").reset());
        //System.out.println(ansi().cursorUpLine().cursorUpLine().bg(Color.RED).a("World!").reset());

        //tui.singleThreadExecutor.submit(SINGLETON_TUI_INSTANCE::titleScreen);
    }
}
