package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConnectionController extends ServerController {

    private static final ConnectionController SINGLETON_INSTANCE = new ConnectionController();

    private ConnectionController() {
    }

    public static ConnectionController getInstance() {
        return SINGLETON_INSTANCE;
    }

    @Override
    public void generatePlayer(NetworkSession sender, String nickname) {
        Player target = new Player(nickname);
        activePlayers.put(sender, target);
        System.out.println("[SERVER]: sending SetNicknameCommand and SetLobbiesCommand to client " + sender);
        sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();
        sender.getListener().notified(
                new SetLobbiesCommand(
                        model.LOBBY_CONTROLLERS.entrySet().stream()
                                .collect(
                                        Collectors.toMap(Map.Entry::getKey, (entry) -> (entry.getValue()).CONTROLLED_LOBBY)
                                )
                )
        );
    }

    @Override
    public void setNickname(NetworkSession sender, String nickname) {
        System.out.println("[CLIENT]: SetNicknameCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        Optional<Player> selectedPlayer = activePlayers.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if (selectedPlayer.isPresent()) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            );
        } else {
            activePlayers.get(sender).setNickname(nickname);
            System.out.println("[SERVER]: sending SetNicknameCommand to client " + sender);
            sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();

            //TODO: update to other players too!
        }
    }

    @Override
    public void createLobby(NetworkSession sender, int maxPlayers) {
        System.out.println("[CLIENT]: CreateLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;
        //TODO: si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        if (maxPlayers < 2 || maxPlayers > 4) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid number of max players (out of range: accepted [2-4])")
                    )
            );
            return;
        }

        Player target = activePlayers.get(sender);
        Lobby lobby = new Lobby(target, maxPlayers);
        UUID lobbyUUID;

        do {
            lobbyUUID = UUID.randomUUID();
        } while (model.LOBBY_CONTROLLERS.containsKey(lobbyUUID));
        //FIXME: remember, when (and if) putting a game back into lobbies, that if gameUUID is contained you must change it!

        LobbyController controller = new LobbyController(lobby);
        model.LOBBY_CONTROLLERS.put(lobbyUUID, controller);
        sender.setController(controller);

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        for (var client : activePlayers.keySet())
            if (!(activePlayers.get(client) instanceof InGamePlayer))
                client.getListener().notified(new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    @Override
    public void joinLobby(NetworkSession sender, UUID lobbyUUID) {
        System.out.println("[CLIENT]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        if (!model.LOBBY_CONTROLLERS.containsKey(lobbyUUID)) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            );
            return;
        }

        LobbyController roomController = model.LOBBY_CONTROLLERS.get(lobbyUUID);

        Lobby lobby = roomController.CONTROLLED_LOBBY;

        if (lobby.getPlayersNumber() >= lobby.getMaxPlayers()) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot join a full lobby")
                    )
            );
            return;
        }

        Player target = activePlayers.get(sender);

        lobby.addPlayer(target);
        sender.setController(roomController);

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for (var client : activePlayers.keySet())
            if (!(activePlayers.get(client) instanceof InGamePlayer))
                client.getListener().notified(new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }
}
