package it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Color;

public class PickColorCommand implements ServerCommand {

    private final Color COLOR;

    public PickColorCommand(Color color) {
        this.COLOR = color;
    }

    @Override
    public void execute(VirtualClient caller, ServerControllerInterface serverController) {
        serverController.pickColor(caller, COLOR);
    }
}
