package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.*;

/*TODO: In case of high traffic volumes on network, we can reduce it by sending the updates to lobby states (creation, updates) only to clients
        which aren't already in a lobby. */
public abstract class ServerController implements ServerControllerInterface {

    public static final ServerModel model = new ServerModel();
    public static final Map<NetworkSession, Player> activePlayers = new HashMap<>();
    public static final Map<String, NetworkSession> inactiveSessions = new HashMap<>();

    protected boolean hasNoPlayer(NetworkSession client) {
        if (!activePlayers.containsKey(client)) {
            client.getListener().notified(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Unregistered client")
                    )
            );
            return true;
        }
        return false;
    }

    private void renewTimeoutTimerTask(NetworkSession target) {
        TimerTask timeoutTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("[SERVER]" + target + " didn't send any keepAlive in 30"
                        + " seconds or the game has sent an update and its state is inconsistent, disconnecting....");
                ControllerInterface thisController = target.getController();
                    if (thisController instanceof GameController)
                        leaveGame(target);
                    else if (thisController instanceof LobbyController)
                        leaveLobby(target, true);

                    cancel();
            }
        };

        target.scheduleTimeoutTimerTask(timeoutTask);
    }

    public void keepAlive(NetworkSession sender) {
        if (hasNoPlayer(sender)) return;

        sender.getTimeoutTask().cancel();
        renewTimeoutTimerTask(sender);
        System.out.println("[CLIENT]: keepAlive command received from " + sender + ". Resetting timeout");
    }

    protected void generatePlayer(NetworkSession sender, String nickname) {
    }

    public void createPlayer(NetworkSession sender, String nickname) {
        if (activePlayers.containsKey(sender)) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Client already registered")
                    )
            );
            return;
        }

        Optional<Player> selectedPlayer = activePlayers.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if (selectedPlayer.isPresent()) {
            System.out.println("[SERVER]: sending an Exception while trying to log in to " + sender);
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            );
            return;
        }

        System.out.println("[CLIENT]: CreatePlayerCommand received and being executed");
        NetworkSession target = inactiveSessions.get(nickname);

        if (target != null)
            sender.setController(target.getController());

        ((ServerController) sender.getController()).generatePlayer(sender, nickname);

        //Creating the timeoutRoutine that will be started in case the client doesn't send a keepAliveCommand in the 60 seconds span.
        renewTimeoutTimerTask(sender);
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