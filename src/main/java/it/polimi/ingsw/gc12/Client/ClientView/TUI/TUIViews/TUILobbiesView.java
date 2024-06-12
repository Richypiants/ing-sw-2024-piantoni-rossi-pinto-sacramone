package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIParser;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
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

    public void lobbiesScreen() {
        clearTerminal();

        int i = 1;
        printToPosition(ansi().cursor(i++,1)
                .fg(Ansi.Color.RED).bold()
                .a("[PLAYER]: ").a(ClientController.getInstance().VIEWMODEL.getOwnNickname()));
        printToPosition(ansi().cursor(i++, 1).a("[CURRENT LOBBY]: " + (
                        ClientController.getInstance().VIEWMODEL.inRoom() ?
                                ClientController.getInstance().VIEWMODEL.getCurrentLobby() : //TODO: stampare UUID?
                                        "none"
                        )
                )
        );
        i++;
        printToPosition(ansi().cursor(i++, 1).a("[OTHER ACTIVE LOBBIES]: "));
        if (ClientController.getInstance().VIEWMODEL.getLobbies().isEmpty())
            printToPosition(ansi().cursor(--i, 25).a("none").cursor(i++, 1));
        else
            for (var lobby : ClientController.getInstance().VIEWMODEL.getLobbies().entrySet())
                if (!lobby.getValue().equals(ClientController.getInstance().VIEWMODEL.getCurrentLobby()))
                    printToPosition(ansi().cursor(i++, 1).a(lobby.getKey() + ": " + lobby.getValue()));

        printToPosition(ansi().cursor(i++, 1));
        printToPosition(ansi().cursor(i, 1).a(
                """
                            'createLobby <maxPlayers>' per creare una lobby,
                            'joinLobby <lobbyUUID>' per joinare una lobby esistente,
                            'setNickname <newNickname>' per cambiare il proprio nickname,
                            'selectColor <color>' per scegliere un colore tra quelli disponibili,
                            'leaveLobby' per lasciare la lobby in cui si e' attualmente,
                            'quit' per ritornare alla schermata del titolo
                """
                //TODO: leaveLobby andrebbe promptato solo dopo
        ));
    }

    public void showNickname() {
        TUIParser.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().VIEWMODEL.getOwnNickname().length();
        lobbiesScreen();
        //printToPosition(ansi().cursor(1, 11).eraseLine(Erase.FORWARD).fg(Ansi.Color.RED).bold()
        //        .a(ClientController.getInstance().ownNickname).eraseLine().reset());
        //TODO: altrimenti: erasare il nickname dalla inputLine (va fatto dopo aver implementato che non si erasa l'inputLine)
    }
}
