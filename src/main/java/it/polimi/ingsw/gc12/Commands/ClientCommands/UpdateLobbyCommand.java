package it.polimi.ingsw.gc12.Commands.ClientCommands;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.Lobby;

/**
 * Represents a client command to update the state of a lobby on the client side.
 * Implements the {@link ClientCommand} interface.
 */
public class UpdateLobbyCommand implements ClientCommand {

    private final Lobby LOBBY;

    /**
     * Constructs an UpdateLobbyCommand with the lobby object containing updated information.
     *
     * @param lobby The lobby object containing updated information.
     */
    public UpdateLobbyCommand(Lobby lobby) {
        this.LOBBY = lobby;
    }

    /**
     * Executes the command on the provided client controller, requesting to update the ViewModel and the View accordingly.
     *
     * @param clientController The client controller interface on which to execute the command.
     */
    @Override
    public void execute(ClientControllerInterface clientController) {
        clientController.updateLobby(LOBBY);
    }
}
