package it.polimi.ingsw.gc12.View.Client.TUI.TUIViews;

import it.polimi.ingsw.gc12.View.Client.TUI.TUIParser;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Singleton class representing the title view in the Terminal User Interface (TUI).
 * It extends the TUIView class and implements methods for displaying the title screen.
 */
public class TUITitleView extends TUIView{

    /**
     * Singleton instance of TUITitleView.
     */
    private static TUITitleView titleView = null;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private TUITitleView() {
        super();
    }

    /**
     * Returns the single instance of TUITitleView, creating it if necessary.
     *
     * @return The singleton instance of TUITitleView.
     */
    public static TUITitleView getInstance() {
        if (titleView == null) {
            titleView = new TUITitleView();
        }
        return titleView;
    }

    /**
     * Displays the title screen which shows the ASCIIArts, waits for user input to proceed, and transitions to the next view state.
     */
    @Override
    public void titleScreen() {
        TUIParser.COMMAND_INPUT_COLUMN = 6 + VIEWMODEL.getOwnNickname().length();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN));
        clearTerminal();

        printToPosition(ansi().cursor(1, 1).a("Starting Codex Naturalis..."));
        clearTerminal();
        printToPosition(ansi()
                .cursor(1, 50).a("                                                                                                                     ")
                .cursorMove(-117, 1).a("                                                         ███████████                                                 ")
                .cursorMove(-117, 1).a("                                                          █████████████                                              ")
                .cursorMove(-117, 1).a("                                                  ███████   ██████████████                                           ")
                .cursorMove(-117, 1).a("                                                ████████████  ██████████████                                         ")
                .cursorMove(-117, 1).a("                                               ███       ████   █████████████                                        ")
                .cursorMove(-117, 1).a("                                              ███          ███    ████████████                                       ")
                .cursorMove(-117, 1).a("                                              ███    ████  ███     ████████████                                      ")
                .cursorMove(-117, 1).a("                                              ████   ███    ██       ███████████                                     ")
                .cursorMove(-117, 1).a("                                                ███████     ███       ██████████                                     ")
                .cursorMove(-117, 1).a("                                                            ███ ██████  █████████                                    ")
                .cursorMove(-117, 1).a("                                                            ████     ██  ████████                                    ")
                .cursorMove(-117, 1).a("                                                            ████  ████     ██████                                    ")
                .cursorMove(-117, 1).a("                                                            ███             █████                                    ")
                .cursorMove(-117, 1).a("                                                            ███               ██                                     ")
                .cursorMove(-117, 1).a("                                            ████████████████████████████████████                                     ")
                .cursorMove(-117, 1).a("                                           ██████████████████████████████████████                                    ")
                .cursorMove(-117, 1).a("                                          ████████████████████████████████████████                                   ")
                .cursorMove(-117, 1).a("                                          ████████████████████████████████████████                                   ")
                .cursorMove(-117, 1).a("                                          █████████       ████████████████████████                                   ")
                .cursorMove(-117, 1).a("                                          ███████          ██████████   ██████████                                   ")
                .cursorMove(-117, 1).a("                                           █████            ██████         ██████                                    ")
                .cursorMove(-117, 1).a("                                          █████            █████           ████                                     ")
                .cursorMove(-117, 1).a("                                           █████            ████            █████                                    ")
                .cursorMove(-117, 1).a("                                           ██████           ████            █████                                    ")
                .cursorMove(-117, 1).a("                                           ██████          ██████           █████                                    ")
                .cursorMove(-117, 1).a("                                           ████████       █████████       ███████                                    ")
                .cursorMove(-117, 1).a("                                            ██████████████████  ███████████████                                      ")
                .cursorMove(-117, 1).a("                              █████           █████████████  █   █████████████                                       ")
                .cursorMove(-117, 1).a("                            ██████████            █████████████████████████                                          ")
                .cursorMove(-117, 1).a("                           ████████  ██            ██████████████████████                                            ")
                .cursorMove(-117, 1).a("                           ██████     ██           ██████████████████████     ███           ███████                  ")
                .cursorMove(-117, 1).a("                          ██████      ███            ███ ███████ █████        ████      ████     ███                 ")
                .cursorMove(-117, 1).a("                         ██████       ███ ██                                  ████    █████████   ███                ")
                .cursorMove(-117, 1).a("                         █████        ███ ███                   ██            ███    ██████████   ███                ")
                .cursorMove(-117, 1).a("                         █████       ███   ██        ███  █     ███  █████           ███████      ███                ")
                .cursorMove(-117, 1).a("                         ████       ███    ████     ████ ████   ███ ███████  ███    ███████       ████               ")
                .cursorMove(-117, 1).a("                         ████   █████      █ ███    ███  ███    ██████████   ███   ███████        ████               ")
                .cursorMove(-117, 1).a("                         ████              █ ████  ████  ███    █████ ████   ███   ███████        ███                ")
                .cursorMove(-117, 1).a("                         ████             ██ ████  ████  ███   █████  ████   ███   ██████        ████                ")
                .cursorMove(-117, 1).a("                         ████             ██ ████  ████  ███   █████  ████  ███   ███████        ████                ")
                .cursorMove(-117, 1).a("                         ████             █  ████ █████ ████   ████   ███   ███   ██████         ████                ")
                .cursorMove(-117, 1).a("                         ████            ██  ████ █████ ████  █████   ███  ████  ███████        ████                 ")
                .cursorMove(-117, 1).a("                          ███            █    ███ █ █████████ █████   ███  █████ ███████        ███                  ")
                .cursorMove(-117, 1).a("                           ██           ██    █████  ████  ████ ██     ███████████ █████       ████                  ")
                .cursorMove(-117, 1).a("                            ██         ██      ███               █     █████  ███  █████       ███                   ")
                .cursorMove(-117, 1).a("                             ██       ███                               ███         ████      ███                    ")
                .cursorMove(-117, 1).a("                              ██   █████                                            █████    ███                     ")
                .cursorMove(-117, 1).a("                                ██████                                                ████ ███                       ")
                .cursorMove(-117, 1).a("                                                                                        ████                         "));
        try {
            sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e); //Should never happen
        }

        clearTerminal();
        printToPosition(ansi()
            .cursor(18,70).fg(214).a("             ██████╗ ██████╗ ██████╗ ███████╗██╗  ██╗                  ")
            .cursorMove(-71, 1).a("            ██╔════╝██╔═══██╗██╔══██╗██╔════╝╚██╗██╔╝                  ")
            .cursorMove(-71, 1).a("            ██║     ██║   ██║██║  ██║█████╗   ╚███╔╝                   ")
            .cursorMove(-71, 1).a("            ██║     ██║   ██║██║  ██║██╔══╝   ██╔██╗                   ")
            .cursorMove(-71, 1).a("            ╚██████╗╚██████╔╝██████╔╝███████╗██╔╝ ██╗                  ")
            .cursorMove(-71, 1).a("             ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝                  ")
            .cursorMove(-71, 2).a("███╗   ██╗ █████╗ ████████╗██╗   ██╗██████╗  █████╗ ██╗     ██╗███████╗")
            .cursorMove(-71, 1).a("████╗  ██║██╔══██╗╚══██╔══╝██║   ██║██╔══██╗██╔══██╗██║     ██║██╔════╝")
            .cursorMove(-71, 1).a("██╔██╗ ██║███████║   ██║   ██║   ██║██████╔╝███████║██║     ██║███████╗")
            .cursorMove(-71, 1).a("██║╚██╗██║██╔══██║   ██║   ██║   ██║██╔══██╗██╔══██║██║     ██║╚════██║")
            .cursorMove(-71, 1).a("██║ ╚████║██║  ██║   ██║   ╚██████╔╝██║  ██║██║  ██║███████╗██║███████║")
            .cursorMove(-71, 1).a("╚═╝  ╚═══╝╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝╚══════╝").reset());

        printToPosition(ansi().cursor(TUIParser.COMMAND_INPUT_ROW - 2, 1).a("Press ENTER to start..."));
        console.readLine();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN).eraseScreen(Ansi.Erase.FORWARD));

        ViewState.getCurrentState().keyPressed();
    }
}
