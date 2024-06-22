package it.polimi.ingsw.gc12.Commands.ServerCommands;

import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Network.NetworkSession;

/**
 * Represents a server command to create a new lobby with a specified maximum number of players.
 * Implements the {@link ServerCommand} interface.
 */
public class CreateLobbyCommand implements ServerCommand {

    private final int MAX_PLAYERS;

    /**
     * Constructs a CreateLobbyCommand with the specified maximum number of players.
     *
     * @param maxPlayers The maximum number of players allowed in the new lobby.
     */
    public CreateLobbyCommand(int maxPlayers) {
        MAX_PLAYERS = maxPlayers;
    }

    /**
     * Executes the command by requesting to the server controller to create a new lobby with the specified maximum players.
     *
     * @param caller           The network session of the client initiating the command (not used in this command).
     * @param serverController The server controller interface to interact with server-side logic.
     */
    @Override
    public void execute(NetworkSession caller, ServerControllerInterface serverController) {
        serverController.createLobby(caller, MAX_PLAYERS);
    }
}
