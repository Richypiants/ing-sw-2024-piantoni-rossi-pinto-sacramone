package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIParser;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
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

    private StringBuilder buildLobbyMessage(Lobby lobby) {
        StringBuilder thisLobbyMessage = new StringBuilder(Ansi.ansi().a(lobby.getRoomUUID() + " -> #MaxPlayers: " + lobby.getMaxPlayers()).reset().toString());

        thisLobbyMessage.append(" | Players: {");
        for (Player player : lobby.getPlayers()) {
            Ansi.Color ansiColor = player.getColor().equals(Color.NO_COLOR) ? Ansi.Color.WHITE : Ansi.Color.valueOf(player.getColor().name());
            String coloredPlayer = Ansi.ansi().fg(ansiColor).a(player.getNickname()).reset().toString();
            thisLobbyMessage.append(coloredPlayer).append(" | ");
        }
        thisLobbyMessage.delete(thisLobbyMessage.length() - 3, thisLobbyMessage.length());

        thisLobbyMessage.append("}");

        thisLobbyMessage.append(" | Available Colors: {");
        for (Color color : lobby.getAvailableColors()) {
            Ansi.Color ansiColor = Ansi.Color.valueOf(color.name());
            String coloredColor = Ansi.ansi().fg(ansiColor).a(color.name()).reset().toString();
            thisLobbyMessage.append(coloredColor).append(" ");
        }
        thisLobbyMessage.deleteCharAt(thisLobbyMessage.length() - 1).append("}");

        return thisLobbyMessage;
    }

    @Override
    public void lobbiesScreen() {
        clearTerminal();
        int i = 1;
        printToPosition(ansi().cursor(i++,1).bold()
                .a("[PLAYER]: ").a(VIEWMODEL.getOwnNickname()));

        printToPosition(ansi().cursor(++i, 1).a(
                """
                                    '[createLobby | cl] <maxPlayers>' to create a new lobby,
                                    '[joinLobby | jl] <lobbyUUID>' to join an existing lobby,
                                    '[setNickname | sn] <newNickname>' to change your own nickname,
                                    '[selectColor | sc] <color>' to choose a color among the available ones,
                                    '[leaveLobby | ll]' to leave the lobby you are currently in,
                                    '[quit]' to go back to title screen.
                """
        ));
        i+=7;

        printToPosition(ansi().cursor(i++, 1).a("[CURRENT LOBBY]: " + (
                        VIEWMODEL.inRoom() ?
                            buildLobbyMessage(VIEWMODEL.getCurrentLobby()) :
                            "none"
                        )));
        i++;
        printToPosition(ansi().cursor(i++, 1).a("[OTHER ACTIVE LOBBIES]: "));
        if (VIEWMODEL.getLobbies().isEmpty())
            printToPosition(ansi().cursor(--i, 25).a("none"));
        else {
            i++;
            for (var lobby : VIEWMODEL.getLobbies().entrySet())
                if (!lobby.getValue().equals(VIEWMODEL.getCurrentLobby()))
                    printToPosition(ansi().cursor(i++, 1).a("    [UUID] " + buildLobbyMessage(lobby.getValue())));
        }
    }

    @Override
    public void showNickname() {
        TUIParser.COMMAND_INPUT_COLUMN = 6 + VIEWMODEL.getOwnNickname().length();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN));
        lobbiesScreen();
    }
}
