package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.StartGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

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
                        lobbiesAndGames.entrySet().stream()
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
        GameLobby lobby = new GameLobby(target, maxPlayers);
        UUID lobbyUUID;

        do {
            lobbyUUID = UUID.randomUUID();
        } while (lobbiesAndGames.containsKey(lobbyUUID));

        lobbiesAndGames.put(lobbyUUID, lobby);
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

        if (!lobbiesAndGames.containsKey(lobbyUUID)) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            );
            return;
        }

        GameLobby lobby = lobbiesAndGames.get(lobbyUUID);

        if (lobby instanceof Game) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("The provided UUID refers to a game and not to a lobby")
                    )
            );
            return;
        }

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

        //TODO: startGame()... && add synchronization
        if (lobby.getPlayersNumber() >= lobby.getMaxPlayers()) {
            Game newGame = new Game(lobby);
            GameController controller = new GameController(newGame);

            lobbiesAndGames.put(lobbyUUID, newGame);

            System.out.println("[SERVER]: sending StartGameCommand to clients starting game");
            //TODO: estrarre la logica di evoluzione dei player da Game (altrimenti, fixare i get) E SINCRONIZZAREEEE
            for (var player : lobby.getPlayers()) {
                VirtualClient targetClient = keyReverseLookup(players, player::equals);
                InGamePlayer targetInGamePlayer = newGame.getPlayers().stream()
                        .filter((inGamePlayer) -> inGamePlayer.getNickname().equals(player.getNickname()))
                        .findFirst()
                        .orElseThrow(); //TODO: strano... gestire?

                players.put(targetClient, targetInGamePlayer);
                playersToControllers.remove(target);
                playersToControllers.put(targetInGamePlayer, controller);

                requestToClient(targetClient, new StartGameCommand(lobbyUUID, newGame.generateDTO(targetInGamePlayer)));

                //FIXME: should clients inform that they are ready before? (ready() method call?)
                //Calls to game creation, generateInitialCards ...
            }
            newGame.getCurrentState().transition();

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & GameLobby?
            while (lobby.getPlayersNumber() > 0) {
                lobby.removePlayer(lobby.getPlayers().getFirst());
            }
        }

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
        //FIXME: risolvere SINCRONIZZANDO su un gameCreationLock
        for (var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); //updateLobby();
    }
}
