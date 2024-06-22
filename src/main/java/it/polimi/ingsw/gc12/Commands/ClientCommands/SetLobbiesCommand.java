package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.Lobby;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a client command to set the lobbies on the client side.
 * Implements the {@link ClientCommand} interface.
 */
public class SetLobbiesCommand implements ClientCommand {

    private final Map<UUID, Lobby> LOBBIES;

    /**
     * Constructs a SetLobbiesCommand with the specified lobbies mapping.
     *
     * @param lobbies The map of UUIDs to lobbies representing the lobbies to set on the client side.
     */
    public SetLobbiesCommand(Map<UUID, Lobby> lobbies) {
        this.LOBBIES = lobbies;
    }

    /**
     * Executes the command on the provided client controller, requesting to set the lobbies using the stored map of lobbies.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.setLobbies(LOBBIES);
    }
}
