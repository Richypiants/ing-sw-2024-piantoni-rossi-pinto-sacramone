package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Model.*;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;

import java.util.Map;
import java.util.NoSuchElementException;
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
    public void generatePlayer(VirtualClient sender, String nickname) {
        Player target = new Player(nickname);
        players.put(sender, target);
        System.out.println("[SERVER]: sending SetNicknameCommand and SetLobbiesCommand to client " + sender);
        requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();
        requestToClient(
                sender,
                new SetLobbiesCommand(
                        model.ROOMS.entrySet().stream()
                                .filter((entry) -> !(entry.getValue() instanceof Game))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
        );
    }

    @Override
    public void setNickname(VirtualClient sender, String nickname) {
        System.out.println("[CLIENT]: SetNicknameCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if (selectedPlayer.isPresent()) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            );
        } else {
            players.get(sender).setNickname(nickname);
            System.out.println("[SERVER]: sending SetNicknameCommand to client " + sender);
            requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();

            //TODO: update to other players too!
        }
    }

    @Override
    public void createLobby(VirtualClient sender, int maxPlayers) {
        System.out.println("[CLIENT]: CreateLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;
        //TODO: si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        if (maxPlayers < 2 || maxPlayers > 4) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid number of max players (out of range: accepted [2-4])")
                    )
            );
            return;
        }

        Player target = players.get(sender);
        Lobby lobby = new Lobby(target, maxPlayers);
        UUID lobbyUUID;

        do {
            lobbyUUID = UUID.randomUUID();
        } while (model.ROOMS.containsKey(lobbyUUID));

        model.ROOMS.put(lobbyUUID, lobby);
        LobbyController controller = new LobbyController(lobby);
        playersToControllers.put(target, controller);

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        for (var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }

    @Override
    public void joinLobby(VirtualClient sender, UUID lobbyUUID) {
        System.out.println("[CLIENT]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        if (!model.ROOMS.containsKey(lobbyUUID)) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            );
            return;
        }

        Room room = model.ROOMS.get(lobbyUUID);

        if (room instanceof Game) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("The provided UUID refers to a game and not to a lobby")
                    )
            );
            return;
        }

        Lobby lobby = (Lobby) room;

        if (lobby.getPlayersNumber() >= lobby.getMaxPlayers()) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot join a full lobby")
                    )
            );
            return;
        }

        Player target = players.get(sender);

        lobby.addPlayer(target);
        playersToControllers.put(target, playersToControllers.values().stream()
                        .filter((serverController) ->
                                serverController instanceof LobbyController &&
                                        ((LobbyController) serverController).CONTROLLED_LOBBY.equals(lobby)
                        ).findAny()
                        .orElseThrow(NoSuchElementException::new)
                //FIXME: non sono sicuro debba essere lanciata questa, e comunque non dovrebbe mai venire lanciata...
        );

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for (var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }
}
