package it.polimi.ingsw.gc12.View.Client.TUI.TUIViews;

import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.View.Client.TUI.TUIParser;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Singleton class representing the lobbies view in the Terminal User Interface (TUI).
 * It extends the TUIView class and implements methods for displaying and interacting with the lobbies.
 */
public class TUILobbiesView extends TUIView{

    /**
     * Singleton instance of TUILobbiesView.
     */
    private static TUILobbiesView lobbiesView = null;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private TUILobbiesView() {
        super();
    }

    /**
     * Returns the single instance of TUILobbiesView, creating it if necessary.
     *
     * @return The singleton instance of TUILobbiesView.
     */
    public static TUILobbiesView getInstance() {
        if (lobbiesView == null) {
            lobbiesView = new TUILobbiesView();
        }
        return lobbiesView;
    }

    /**
     * Builds a string representation of a lobby, including its UUID, maximum players, current players,
     * and available colors (printed with ANSI colors!).
     *
     * @param lobby The lobby to build the message for.
     * @return A StringBuilder containing the formatted lobby information.
     */
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

    /**
     * Displays the lobbies screen, showing the player's nickname, available commands, current lobby,
     * and other active lobbies.
     */
    @Override
    public void lobbiesScreen() {
        clearTerminal();
        int i = 1;
        printToPosition(ansi().cursor(i++,1).bold()
                .a("[PLAYER]: ").a(VIEWMODEL.getOwnNickname()));

        for (var command : ViewState.getCurrentState().TUICommands)
            printToPosition(ansi().cursor(++i, 1).a(command));

        i += 2;

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
            for (var lobby : VIEWMODEL.getLobbies().values())
                if (!lobby.equals(VIEWMODEL.getCurrentLobby()))
                    printToPosition(ansi().cursor(i++, 1).a("    [UUID] " + buildLobbyMessage(lobby)));
        }
    }

    /**
     * Updates the player's nickname and redisplays the lobbies screen.
     */
    @Override
    public void showNickname() {
        TUIParser.COMMAND_INPUT_COLUMN = 6 + VIEWMODEL.getOwnNickname().length();
        System.out.print(ansi().cursor(TUIParser.COMMAND_INPUT_ROW, TUIParser.COMMAND_INPUT_COLUMN));
        lobbiesScreen();
    }
}
