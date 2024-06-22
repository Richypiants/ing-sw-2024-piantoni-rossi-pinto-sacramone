package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;

/**
 * Represents a server command for a client to pick a color.
 * Implements the {@link ServerCommand} interface.
 */
public class PickColorCommand implements ServerCommand {

    private final Color COLOR;

    /**
     * Constructs a PickColorCommand with the specified color.
     *
     * @param color The color chosen by the client.
     */
    public PickColorCommand(Color color) {
        this.COLOR = color;
    }

    /**
     * Executes the command by requesting to the server controller to handle the client's color selection.
     *
     * @param caller           The network session of the client initiating the command.
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.pickColor(caller, COLOR);
    }
}

