package it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIListener;
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
                .a("[PLAYER]: ").a(ClientController.getInstance().viewModel.getOwnNickname()));
        printToPosition(ansi().cursor(i++, 1).a("[CURRENT LOBBY]: " + (
                                ClientController.getInstance().viewModel.inLobbyOrGame() ?
                                        ClientController.getInstance().viewModel.getCurrentLobby() : //TODO: stampare UUID?
                                        "none"
                        )
                )
        );
        printToPosition(ansi().cursor(i++, 1).a("[ACTIVE LOBBIES] "));
        for (var entry : ClientController.getInstance().viewModel.getLobbies().entrySet()) {
            printToPosition(ansi().cursor(i++, 1).a(entry.getKey() + ": " + entry.getValue()));
        }
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
        TUIListener.COMMAND_INPUT_COLUMN = 6 + ClientController.getInstance().viewModel.getOwnNickname().length();
        lobbiesScreen();
        //printToPosition(ansi().cursor(1, 11).eraseLine(Erase.FORWARD).fg(Ansi.Color.RED).bold()
        //        .a(ClientController.getInstance().ownNickname).eraseLine().reset());
        //TODO: altrimenti: erasare il nickname dalla inputLine (va fatto dopo aver implementato che non si erasa l'inputLine)
    }
}
