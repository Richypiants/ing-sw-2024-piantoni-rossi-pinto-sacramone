package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.EndGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ConnectionController;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
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

    //TODO: send steps in points calculation process for flavour?
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

        /**
         * OLD CODE that transforms the game into a lobby.
         * At the moment we're destroying the lobby.
         *
         *
        //Removing disconnected players (we were keeping them until end of game hoping they would reconnect)
        for (var player : players)
            if(!player.isActive())
                ServerController.getInstance().playersToLobbiesAndGames.remove(player);

        UUID lobbyUUID = keyReverseLookup(ServerController.getInstance().lobbiesAndGames, GAME::equals);
         Lobby returnLobby = GAME.toLobby();

        ServerController.getInstance().lobbiesAndGames.put(lobbyUUID, returnLobby);

        for(var player : returnLobby.getPlayers()) {
            ServerController.getInstance().players.put(
                    keyReverseLookup(ServerController.getInstance().players, player::equals),
                    player
            );
            ServerController.getInstance().playersToLobbiesAndGames.put(player, returnLobby);
        }

        System.out.println("[SERVER]: Sending lobbies to clients previously in "+ GAME.toString());

        try {
            // Sending lobbies list to players who were in this game (because they didn't have it updated)
            for (var target : returnLobby.getPlayers()) {
                keyReverseLookup(ServerController.getInstance().players, target::equals)
                        //TODO : Handle exceptions in the correct way and not like this
                        .requestToClient(
                                new SetLobbiesCommand(
                                        ServerController.getInstance().lobbiesAndGames.entrySet().stream()
                                                .filter((entry) -> !(entry.getValue() instanceof Game))
                                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                                )
                        );
            }

            System.out.println("[SERVER]: Recreating lobby with clients in "+ GAME.toString());
            // Update lobbies' lists of all other active players
            for (var client : ServerController.getInstance().players.keySet())
                if (!(ServerController.getInstance().players.get(client) instanceof InGamePlayer))
                    //TODO : Handle exceptions in the correct way and not like this
                    client.requestToClient(new UpdateLobbyCommand(lobbyUUID, returnLobby)); //updateLobby();

        } //TODO: This will be deleted or well-handled.
        catch (Throwable e) {
            throw new RuntimeException(e);
        }**/

        //Clearing the mappings to the game


        //Removing all active and inactive players from the Map containing all the mappings.
        for (var player : GAME.getPlayers())
            if (player.isActive())
                GAME_CONTROLLER.getSessionFromActivePlayer(player).setController(ConnectionController.getInstance());
            else
                GameController.INACTIVE_SESSIONS.remove(player.getNickname());

        //FIXME: Using a Lobby to convert the instances of InGamePlayer to Player and then discarding it. Better solutions?
        // If putting players back into lobby, remember to re-add listeners to the lobby
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

                GameController.MODEL.addListener(thisSession.getListener());

                currentIndex++;
            }
        }

        //TODO: add players to a new lobby now that the game doesn't start until the colors are chosen?
        // and eventually move code above this instruction inside destroyGameController
        GameController.MODEL.destroyGameController(GAME_CONTROLLER);
    }
}
