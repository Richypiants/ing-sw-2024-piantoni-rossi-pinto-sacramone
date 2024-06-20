package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Commands.ClientCommands.EndGameCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.Server.ConnectionController;
import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class VictoryCalculationState extends GameState {

    public VictoryCalculationState(GameController controller, Game thisGame) {
        super(controller, thisGame, "victoryCalculationState");
    }

    @Override
    public void playerDisconnected(InGamePlayer target) {}

    @Override
    public void transition() {
        ArrayList<InGamePlayer> players = GAME.getPlayers();
        List<Triplet<String, Integer, Integer>> pointsStats = new ArrayList<>();
        for (InGamePlayer target : players) {
            if(target.getSecretObjective() != null){
                int playerObjectivePoints = target.getSecretObjective().awardPoints(target) +
                        Arrays.stream(GAME.getCommonObjectives())
                                .mapToInt((objective) -> objective.awardPoints(target))
                                .sum();
                target.increasePoints(playerObjectivePoints);
                pointsStats.add(new Triplet<>(target.getNickname(), target.getPoints(), playerObjectivePoints));
                }
            else pointsStats.add(new Triplet<>(target.getNickname(), -1, -1));
        }

        pointsStats.sort(Comparator.comparingInt(Triplet<String, Integer, Integer>::getY)
                .thenComparingInt(Triplet::getZ)
        );
        pointsStats = new ArrayList<>(pointsStats.reversed());

        //If there's only one player connected to the game, he's the winner and placed #1 in the leaderboard.
        boolean gameEndedDueToDisconnections = GAME.getActivePlayers().size() == 1;
        if(gameEndedDueToDisconnections){
            String winnerNickname = GAME.getActivePlayers().getFirst().getNickname();
            Triplet<String, Integer, Integer> foundEntry = null;
            for(Triplet<String, Integer, Integer> entry : pointsStats) {
                if (entry.getX().equals(winnerNickname)) {
                    foundEntry = entry;
                }
            }
            pointsStats.remove(foundEntry);
            pointsStats.addFirst(foundEntry);
        }

        System.out.println("[SERVER]: Sending leaderboard stats to clients in "+ GAME);
        // Sending leaderboard stats
        GAME.notifyListeners(new EndGameCommand(pointsStats, gameEndedDueToDisconnections));

        //Removing all active and inactive players from the Map containing all the mappings.
        for (var player : GAME.getPlayers())
            if (player.isActive())
                GAME_CONTROLLER.getSessionFromActivePlayer(player).setController(ConnectionController.getInstance());
            else
                (GameController.INACTIVE_SESSIONS.remove(player.getNickname())).setController(ConnectionController.getInstance());

        //Re-creating the lobby to re-convert the InGamePlayers in normal Players,and then discarding it
        //In future releases, one might consider keeping this lobby and the players in it, so that they
        // can start a new game if they want.
        Lobby returnLobby = GAME.toLobby();

        System.out.println("[SERVER]: Sending lobbies to clients previously in " + GAME);

        GameController.MODEL.LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            GAME.notifyListeners(new SetLobbiesCommand(GameController.MODEL.getLobbiesMap()));
        } finally {
            GameController.MODEL.LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }

        int currentIndex = 0;
        synchronized (GAME_CONTROLLER) {
            for (var inGamePlayer : GAME.getActivePlayers()) {
                Player thisPlayer = returnLobby.getPlayers().get(currentIndex);
                NetworkSession thisSession = GAME_CONTROLLER.getSessionFromActivePlayer(inGamePlayer);
                thisSession.setPlayer(thisPlayer);
                GAME_CONTROLLER.putActivePlayer(thisSession, thisPlayer);

                GAME.removeListener(thisSession.getListener());
                inGamePlayer.removeListener(thisSession.getListener());
                GameController.MODEL.addListener(thisSession.getListener());

                currentIndex++;
            }
        }

        GameController.MODEL.destroyGameController(GAME_CONTROLLER);
    }
}
