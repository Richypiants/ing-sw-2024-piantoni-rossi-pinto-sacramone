package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command to create a new player with a specified nickname.
 * Implements the {@link ServerCommand} interface.
 */
public class CreatePlayerCommand implements ServerCommand {

    private final String NICKNAME;

    /**
     * Constructs a CreatePlayerCommand with the specified nickname for the new player.
     *
     * @param nickname The nickname of the new player to be created.
     */
    public CreatePlayerCommand(String nickname) {
        NICKNAME = nickname;
    }

    /**
     * Executes the command by requesting to the server controller to create a new player with the specified nickname.
     *
     * @param caller           The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.createPlayer(caller, NICKNAME);
    }
}
