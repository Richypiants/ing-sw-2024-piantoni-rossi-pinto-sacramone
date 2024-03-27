package it.polimi.ingsw.gc12.ServerController.Command.Game;

import it.polimi.ingsw.gc12.ServerController.Command.Command;
import it.polimi.ingsw.gc12.ServerModel.Player;

import java.util.ArrayList;

public class PlaceInitialCardCommand extends Command {

    private final static Command THIS_SINGLETON_COMMAND = new PlaceInitialCardCommand();

    private PlaceInitialCardCommand() {
        super();
    }

    @Override
    public void execute(Player commandCaller, ArrayList<Object> args) {

        /*gamesMap.get(player).currentState()
                .placeInitialCards((InGamePlayer) commandCaller, (...) objects.get(0), (...) objects.get(1));*/
    }
}
