package it.polimi.ingsw.gc12.ServerController.Command.Chat;

import it.polimi.ingsw.gc12.ServerController.Command.Command;
import it.polimi.ingsw.gc12.ServerModel.Player;

import java.util.ArrayList;

public class DirectMessageCommand extends Command {

    private final static Command THIS_SINGLETON_COMMAND = new DirectMessageCommand();

    private DirectMessageCommand() {
        super();
    }

    @Override
    public void execute(Player commandCaller, ArrayList<Object> args) {

    }
}
