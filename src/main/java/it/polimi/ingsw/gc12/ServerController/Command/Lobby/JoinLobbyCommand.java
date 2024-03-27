package it.polimi.ingsw.gc12.ServerController.Command.Lobby;

import it.polimi.ingsw.gc12.ServerController.Command.Command;
import it.polimi.ingsw.gc12.ServerModel.Player;

import java.util.ArrayList;

public class JoinLobbyCommand extends Command {

    private final static Command THIS_SINGLETON_COMMAND = new JoinLobbyCommand();

    private JoinLobbyCommand() {
        super();
    }

    @Override
    public void execute(Player commandCaller, ArrayList<Object> args) {

    }
}
