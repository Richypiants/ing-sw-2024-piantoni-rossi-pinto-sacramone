package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;
import static it.polimi.ingsw.gc12.Utilities.Commons.varargsToArrayList;

public class VictoryCalculationState extends GameState {

    public VictoryCalculationState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter);
    }

    //TODO: send steps in points calculation process for flavour?
    @Override
    public void transition() {
        ArrayList<InGamePlayer> players = GAME.getPlayers();
        ArrayList<Triplet<String, Integer, Integer>> pointsStats = new ArrayList<>();
        for (InGamePlayer target : players) {
            int playerObjectivePoints = target.getSecretObjective().awardPoints(target) +
                    Arrays.stream(GAME.getCommonObjectives())
                            .mapToInt((objective) -> objective.awardPoints(target))
                            .sum();
            target.increasePoints(playerObjectivePoints);
            pointsStats.add(new Triplet<>(target.getNickname(), target.getPoints(), playerObjectivePoints));
        }

        pointsStats.sort(Comparator.comparingInt(Triplet<String, Integer, Integer>::getY)
                .thenComparingInt(Triplet::getZ)
        );
        pointsStats.reversed();

        //TODO : Handle exceptions in the correct way and not like this
        try {
            // Sending leaderboard stats
            for (var target : players) {
                keyReverseLookup(ServerController.players, target::equals)
                        .requestToServer(varargsToArrayList("endGame", pointsStats));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        //TODO: here we should destroy the file with the saved serialized data

        //Removing disconnected players (we were keeping them until end of game hoping they would reconnect)
        for (var player : players)
            if(!player.isActive())
                ServerController.playersToLobbiesAndGames.remove(player);

        UUID lobbyUUID = keyReverseLookup(ServerController.lobbiesAndGames, GAME::equals);
        GameLobby returnLobby = GAME.toLobby();

        ServerController.lobbiesAndGames.put(lobbyUUID, returnLobby);

        for(var player : returnLobby.getPlayers()) {
            ServerController.players.put(
                    keyReverseLookup(ServerController.players, player::equals),
                    player
            );
            ServerController.playersToLobbiesAndGames.put(player, returnLobby);
        }

        try {
            // Sending lobbies list to players who were in this game (because they didn't have it updated)
            for (var target : returnLobby.getPlayers()) {
                keyReverseLookup(ServerController.players, target::equals)
                        //TODO : Handle exceptions in the correct way and not like this
                        .requestToServer(
                                varargsToArrayList(
                                        "setLobbies",
                                        ServerController.lobbiesAndGames.entrySet().stream()
                                                .filter((entry) -> !(entry.getValue() instanceof Game))
                                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                                )
                        );
            }

            // Update lobbies' lists of all other active players
            for (var client : ServerController.players.keySet())
                if (!(ServerController.players.get(client) instanceof InGamePlayer))
                    //TODO : Handle exceptions in the correct way and not like this
                    client.requestToServer(varargsToArrayList("updateLobby", lobbyUUID, returnLobby)); //updateLobby();

        } //TODO: This will be deleted or well-handled.
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
