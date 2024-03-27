package it.polimi.ingsw.gc12.ServerController.Command;

import it.polimi.ingsw.gc12.ServerModel.Player;

import java.util.ArrayList;

public class KeepAliveCommand extends Command {

    private final static Command THIS_SINGLETON_COMMAND = new KeepAliveCommand();

    private KeepAliveCommand() {
        super();
    }

    @Override
    public void execute(Player commandCaller, ArrayList<Object> args) {

    }
}
