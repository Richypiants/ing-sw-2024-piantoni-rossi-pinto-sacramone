package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIParser;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import static java.lang.Thread.sleep;
import static org.fusesource.jansi.Ansi.ansi;

public class TUITitleView extends TUIView{

    private static TUITitleView titleView = null;

    private TUITitleView() {
        super();
    }

    public static TUITitleView getInstance() {
        if (titleView == null) {
            titleView = new TUITitleView();
        }
        return titleView;
    }

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
            CLIENT_CONTROLLER.ERROR_LOGGER.log(e);
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
