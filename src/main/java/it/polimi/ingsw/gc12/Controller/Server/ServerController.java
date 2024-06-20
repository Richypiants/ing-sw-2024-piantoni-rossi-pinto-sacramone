package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public abstract class ServerController implements ServerControllerInterface {

    public static final ServerModel MODEL = new ServerModel();
    public static final ConcurrentHashMap<String, NetworkSession> INACTIVE_SESSIONS = new ConcurrentHashMap<>();
    protected static final Map<NetworkSession, Player> ACTIVE_PLAYERS = new HashMap<>();
    protected static final ReentrantReadWriteLock ACTIVE_PLAYERS_LOCK = new ReentrantReadWriteLock();

    public void putActivePlayer(NetworkSession session, Player player) {
        ACTIVE_PLAYERS_LOCK.writeLock().lock();
        try {
            ACTIVE_PLAYERS.put(session, player);
        } finally {
            ACTIVE_PLAYERS_LOCK.writeLock().unlock();
        }
    }

    public NetworkSession getSessionFromActivePlayer(Player player) {
        ACTIVE_PLAYERS_LOCK.readLock().lock();
        try {
            return keyReverseLookup(ServerController.ACTIVE_PLAYERS, player::equals);
        } finally {
            ACTIVE_PLAYERS_LOCK.readLock().unlock();
        }
    }

    protected void removeActivePlayer(NetworkSession session) {
        ACTIVE_PLAYERS_LOCK.writeLock().lock();
        try {
            ACTIVE_PLAYERS.remove(session);
        } finally {
            ACTIVE_PLAYERS_LOCK.writeLock().unlock();
        }
    }

    protected boolean hasNoPlayer(NetworkSession client) {
        if (client.getPlayer() == null) {
            client.getListener().notified(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Unregistered client")
                    )
            );
            return true;
        }
        return false;
    }

    public void renewTimeoutTimerTask(NetworkSession target) {
        TimerTask timeoutTask = createTimeoutTask(target);
        target.scheduleTimeoutTimerTask(timeoutTask);
    }

    protected TimerTask createTimeoutTask(NetworkSession target) {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println("[SERVER]" + target + " didn't send any keepAlive in 15"
                        + " seconds or the game has sent an update and its state is inconsistent, disconnecting...");
                ControllerInterface thisController = target.getController();
                if (thisController instanceof GameController)
                    ((GameController) thisController).leaveGame(target);
                else if (thisController instanceof LobbyController)
                    ((LobbyController) thisController).leaveLobby(target, true);
                else {
                    removeActivePlayer(target);
                    MODEL.removeListener(target.getListener());
                }

                cancel();
            }
        };
    }

    public void keepAlive(NetworkSession sender) {
        System.out.println("[CLIENT]: keepAlive command received from " + sender + ". Resetting timeout");
        sender.getTimeoutTask().cancel();
        renewTimeoutTimerTask(sender);
    }

    protected void generatePlayer(NetworkSession sender, String nickname) {
    }

    public void createPlayer(NetworkSession sender, String nickname) {
        if (sender.getPlayer() != null) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Client already registered")
                    )
            );
            return;
        }

        ACTIVE_PLAYERS_LOCK.writeLock().lock();
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
                return;
            }

            //Creating the timeoutRoutine that will be started in case the client doesn't send a keepAliveCommand in the 30 seconds span.
            renewTimeoutTimerTask(sender);

            System.out.println("[CLIENT]: CreatePlayerCommand received and being executed");
            NetworkSession target = INACTIVE_SESSIONS.get(nickname);
            if (target != null) {
                INACTIVE_SESSIONS.remove(nickname);
                sender.setController(target.getController());
            }

            ((ServerController) sender.getController()).generatePlayer(sender, nickname);
        } finally {
            ACTIVE_PLAYERS_LOCK.writeLock().unlock();
        }
    }

    public void setNickname(NetworkSession sender, String nickname) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    public void createLobby(NetworkSession sender, int maxPlayers) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    public void joinLobby(NetworkSession sender, UUID lobbyUUID) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    public void pickColor(NetworkSession sender, Color color) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a lobby!"))
        );
    }

    public void leaveLobby(NetworkSession sender, boolean isInactive) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a lobby!"))
        );
    }

    public void placeCard(NetworkSession sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void pickObjective(NetworkSession sender, int cardID) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void drawFromDeck(NetworkSession sender, String deck) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void drawFromVisibleCards(NetworkSession sender, String deck, int position) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void leaveGame(NetworkSession sender) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void broadcastMessage(NetworkSession sender, String message) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void directMessage(NetworkSession sender, String receiverNickname, String message) {
        sender.getListener().notified(
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }
}