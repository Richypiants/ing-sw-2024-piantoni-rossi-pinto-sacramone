package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIParser;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

public class TUILobbiesView extends TUIView{

    private static TUILobbiesView lobbiesView = null;

    private TUILobbiesView() {
        super();
    }

    public static TUILobbiesView getInstance() {
        if (lobbiesView == null) {
            lobbiesView = new TUILobbiesView();
        }
        return lobbiesView;
    }

    @Override
    public void lobbiesScreen() {
        clearTerminal();

        int i = 1;
        printToPosition(ansi().cursor(i++,1)
                .fg(Ansi.Color.RED).bold()
                .a("[PLAYER]: ").a(VIEWMODEL.getOwnNickname()));
        printToPosition(ansi().cursor(i++, 1).a("[CURRENT LOBBY]: " + (
                        VIEWMODEL.inRoom() ?
                                VIEWMODEL.getCurrentLobby() : //TODO: stampare UUID?
                                        "none"
                        )
                )
        );
        i++;
        printToPosition(ansi().cursor(i++, 1).a("[OTHER ACTIVE LOBBIES]: "));
        if (VIEWMODEL.getLobbies().isEmpty())
            printToPosition(ansi().cursor(--i, 25).a("none").cursor(i++, 1));
        else
            for (var lobby : VIEWMODEL.getLobbies().entrySet())
                if (!lobby.getValue().equals(VIEWMODEL.getCurrentLobby()))
                    printToPosition(ansi().cursor(i++, 1).a(lobby.getKey() + ": " + lobby.getValue()));

        printToPosition(ansi().cursor(i++, 1));
        printToPosition(ansi().cursor(i, 1).a(
                """
                                    '[createLobby | cl] <maxPlayers>' to create a new lobby,
                                    '[joinLobby | jl] <lobbyUUID>' to join an existing lobby,
                                    '[setNickname | sn] <newNickname>' to change your own nickname,
                                    '[selectColor | sc] <color>' to choose a color among the available ones,
                                    '[leaveLobby | ll]' to leave the lobby you are currently in,
                                    '[quit]' to go back to title screen.
                """
                //TODO: leaveLobby andrebbe promptato solo dopo
        ));
    }

    @Override
    public void showNickname() {
        TUIParser.COMMAND_INPUT_COLUMN = 6 + VIEWMODEL.getOwnNickname().length();
        lobbiesScreen();
    }
}
