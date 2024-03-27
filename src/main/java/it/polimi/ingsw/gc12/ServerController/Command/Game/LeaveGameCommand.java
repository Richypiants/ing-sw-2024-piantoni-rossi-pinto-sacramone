package it.polimi.ingsw.gc12.ServerController.Command.Game;

import it.polimi.ingsw.gc12.ServerController.Command.Command;
import it.polimi.ingsw.gc12.ServerModel.Player;

import java.util.ArrayList;

public class LeaveGameCommand extends Command {

    private final static Command THIS_SINGLETON_COMMAND = new LeaveGameCommand();

    private LeaveGameCommand() {
        super();
    }

    @Override
    public void execute(Player commandCaller, ArrayList<Object> args) {

    }
}
