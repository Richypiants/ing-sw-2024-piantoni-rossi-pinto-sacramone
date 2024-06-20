package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.StartGameCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.Server.Server;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;

import java.util.Arrays;

public class LobbyController extends ServerController {

    public final Lobby CONTROLLED_LOBBY;

    public LobbyController(Lobby controlledLobby) {
        this.CONTROLLED_LOBBY = controlledLobby;
    }

    @Override
    public synchronized void pickColor(NetworkSession sender, Color color) {
        System.out.println("[CLIENT]: PickColorCommand received and being executed");

        if (Arrays.stream(Color.values()).noneMatch(color::equals))
            sender.getListener().notified(
                    new ThrowExceptionCommand(new IllegalArgumentException("The specified color doesn't exist"))
            );

        try {
            CONTROLLED_LOBBY.assignColor(sender.getPlayer(), color);
        } catch (UnavailableColorException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new UnavailableColorException("The specified color is not available for this lobby")
                    )
            );
            return;
        }

        if (CONTROLLED_LOBBY.getAvailableColors().size() <= 4 - CONTROLLED_LOBBY.getMaxPlayers()) {
            ACTIVE_PLAYERS_LOCK.writeLock().lock();
            try {
                Game newGame = new Game(CONTROLLED_LOBBY);

                System.out.println("[SERVER]: sending StartGameCommand to clients starting game");

                for (var player : CONTROLLED_LOBBY.getPlayers()) {
                    NetworkSession targetClient = getSessionFromActivePlayer(player);
                    InGamePlayer targetInGamePlayer = newGame.getPlayers().stream()
                            .filter((inGamePlayer) -> inGamePlayer.getNickname().equals(player.getNickname()))
                            .findFirst()
                            .orElseThrow();

                    putActivePlayer(targetClient, targetInGamePlayer);
                    targetClient.setPlayer(targetInGamePlayer);

                    MODEL.removeListener(targetClient.getListener());
                    newGame.addListener(targetClient.getListener());
                    targetInGamePlayer.addListener(targetClient.getListener());

                    targetClient.getListener().notified(new StartGameCommand(newGame.generateDTO(targetInGamePlayer)));
                }

                MODEL.destroyLobbyController(this);
                GameController controller = MODEL.createGameController(newGame);

                for (var inGamePlayer : newGame.getPlayers())
                    getSessionFromActivePlayer(inGamePlayer).setController(controller);
            } finally {
                ACTIVE_PLAYERS_LOCK.writeLock().unlock();
            }
        }
    }

    @Override
    public void leaveLobby(NetworkSession sender, boolean isInactive) {
        System.out.println("[CLIENT]: LeaveLobbyCommand received and being executed");

        if (isInactive) {
            sender.getTimeoutTask().cancel();
            MODEL.removeListener(sender.getListener());
            removeActivePlayer(sender);
        }

        Server.getInstance().commandExecutorsPool.submit(() -> {
            synchronized (this) {
                if (CONTROLLED_LOBBY.getPlayersNumber() == 1)
                    MODEL.destroyLobbyController(this);
                else
                    MODEL.removePlayerFromLobby(sender.getPlayer(), CONTROLLED_LOBBY);
            }
        });

        sender.setController(ConnectionController.getInstance());
    }
}
