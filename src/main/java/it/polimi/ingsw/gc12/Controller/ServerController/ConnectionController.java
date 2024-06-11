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

        MODEL.LOBBY_CONTROLLERS_LOCK.readLock().lock();
        sender.getListener().notified(new SetLobbiesCommand(MODEL.getLobbiesMap()));
        MODEL.LOBBY_CONTROLLERS_LOCK.readLock().unlock();

        sender.setPlayer(target);
        putActivePlayer(sender, target);
        MODEL.addListener(sender.getListener());
    }

    @Override
    public void setNickname(NetworkSession sender, String nickname) {
        System.out.println("[CLIENT]: SetNicknameCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        ACTIVE_PLAYERS_LOCK.readLock().lock();
        try {
            Optional<Player> selectedPlayer = ACTIVE_PLAYERS.values().stream()
                    .filter((player) -> player.getNickname().equals(nickname))
                    .findAny();

            if (selectedPlayer.isPresent()) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new IllegalArgumentException("Provided nickname is already taken")
                        )
                );
            } else {
                sender.getPlayer().setNickname(nickname);
                System.out.println("[SERVER]: sending SetNicknameCommand to client " + sender);
                sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();
            }
        } finally {
            ACTIVE_PLAYERS_LOCK.readLock().unlock();
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

        UUID lobbyUUID;
        LobbyController controller;

        MODEL.LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            do {
                lobbyUUID = UUID.randomUUID();
            } while (MODEL.getLobbyController(lobbyUUID) != null || MODEL.getGameController(lobbyUUID) != null);

            Lobby lobby = new Lobby(lobbyUUID, sender.getPlayer(), maxPlayers);
            controller = MODEL.createLobbyController(lobby);
        } finally {
            MODEL.LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
        sender.setController(controller);

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
    }

    @Override
    public void joinLobby(NetworkSession sender, UUID lobbyUUID) {
        System.out.println("[CLIENT]: JoinLobbyCommand received and being executed");
        if (hasNoPlayer(sender)) return;

        try {
            MODEL.addPlayerToLobby(sender.getPlayer(), lobbyUUID);
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
        sender.setController(MODEL.getLobbyController(lobbyUUID));

        System.out.println("[SERVER]: sending UpdateLobbyCommand to clients");
    }
}
