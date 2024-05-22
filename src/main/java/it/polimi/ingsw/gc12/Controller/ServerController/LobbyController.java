package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.util.UUID;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class LobbyController extends ServerController {

    private final GameLobby CONTROLLED_LOBBY;

    public LobbyController(GameLobby controlledLobby) {
        this.CONTROLLED_LOBBY = controlledLobby;
    }

    @Override
    public void leaveLobby(VirtualClient sender, boolean isInactive) {
        System.out.println("[CLIENT]: LeaveLobbyCommand received and being executed");

        Player target = players.get(sender);

        GameLobby lobby = ((LobbyController) playersToControllers.get(target)).CONTROLLED_LOBBY;
        UUID lobbyUUID = keyReverseLookup(lobbiesAndGames, lobby::equals);
        //Assuming that lobby is contained (thus maps are coherent): check with synchronization that this
        // invariant holds

        lobby.removePlayer(target);
        playersToControllers.remove(target);

        if (lobby.getPlayers().isEmpty()) {
            lobbiesAndGames.remove(lobbyUUID);
        }

        if (isInactive)
            players.remove(sender);

        System.out.println("[SERVER]: sending UpdateLobbiesCommand to clients");

        for (var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); // updateLobby();
    }
}
