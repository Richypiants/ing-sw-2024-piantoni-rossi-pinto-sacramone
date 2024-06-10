package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;

import java.util.Optional;
import java.util.UUID;

public class ConnectionController extends ServerController {

    private static final ConnectionController SINGLETON_INSTANCE = new ConnectionController();

    private ConnectionController() {
    }

    public static ConnectionController getInstance() {
        return SINGLETON_INSTANCE;
    }

    @Override
    protected void generatePlayer(NetworkSession sender, String nickname) {
        Player target = new Player(nickname);

        System.out.println("[SERVER]: sending SetNicknameCommand and SetLobbiesCommand to client " + sender);
        sender.getListener().notified(new SetNicknameCommand(nickname));

        model.LOBBY_CONTROLLERS_LOCK.readLock().lock();
        sender.getListener().notified(new SetLobbiesCommand(model.getLobbiesMap()));
        model.LOBBY_CONTROLLERS_LOCK.readLock().unlock();

        activePlayers.put(sender, target);
        model.addListener(sender.getListener());
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
        }
    }

    @Override
    public void createLobby(NetworkSession sender, int maxPlayers) {
        System.out.println("[CLIENT]: CreateLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        if (maxPlayers < 2 || maxPlayers > 4) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid number of max players (out of range: accepted [2-4])")
                    )
            );
            return;
        }

        Player target = activePlayers.get(sender);
        UUID lobbyUUID;
        LobbyController controller;

        model.LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            do {
                lobbyUUID = UUID.randomUUID();
            } while (model.getLobbyController(lobbyUUID) != null || model.getGameController(lobbyUUID) != null);

            Lobby lobby = new Lobby(lobbyUUID, target, maxPlayers);
            controller = model.createLobbyController(lobby);
        } finally {
            model.LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
        sender.setController(controller);

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
    }

    @Override
    public void joinLobby(NetworkSession sender, UUID lobbyUUID) {
        System.out.println("[CLIENT]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        Player target = activePlayers.get(sender);

        try {
            model.addPlayerToLobby(target, lobbyUUID);
        } catch (IllegalArgumentException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("There's no lobby with the provided UUID")
                    )
            );
        } catch (FullLobbyException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new FullLobbyException("Cannot join a full lobby")
                    )
            );
            return;
        }
        sender.setController(model.getLobbyController(lobbyUUID));

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
    }
}
