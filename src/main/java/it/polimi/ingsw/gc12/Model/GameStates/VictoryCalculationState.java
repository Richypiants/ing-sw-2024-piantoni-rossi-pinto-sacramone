package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import static it.polimi.ingsw.gc12.Utilities.Commons.*;

import java.util.*;
import java.util.stream.Collectors;

public class VictoryCalculationState extends GameState {

    public VictoryCalculationState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter);
    }

    private int calculateAllObjectivesPoints(InGamePlayer target) {
        return target.getSecretObjective().awardPoints(target) +
                Arrays.stream(GAME.getCommonObjectives())
                        .mapToInt((objective) -> objective.awardPoints(target))
                        .sum();
    }

    @Override
    public void transition() {
        //FIXME: dichiarare ogni volta un nuovo array è abbastanza orribile... togliere functional?
        ArrayList<InGamePlayer> players = GAME.getPlayers();
        for (InGamePlayer target : players) {
            target.increasePoints(calculateAllObjectivesPoints(target));
        }

        players.sort((p1, p2) -> p2.getPoints() - p1.getPoints());

        ArrayList<InGamePlayer> tiedPlayers = players.stream()
                .filter((player) -> player.getPoints() ==
                                players.stream()
                                        .mapToInt(InGamePlayer::getPoints)
                                        .max()
                                        .getAsInt()
                        //orElse(0)
                )
                .collect(Collectors.toCollection(ArrayList::new));

        if (tiedPlayers.size() > 1) {
            tiedPlayers.sort((p1, p2) -> calculateAllObjectivesPoints(p2) - calculateAllObjectivesPoints(p1));

            ArrayList<InGamePlayer> tiedObjectivePointsPlayers = tiedPlayers.stream()
                    .filter((player) -> calculateAllObjectivesPoints(player) ==
                                    tiedPlayers.stream()
                                            .mapToInt(this::calculateAllObjectivesPoints)
                                            .max()
                                            .getAsInt()
                            //orElse(0)
                    )
                    .collect(Collectors.toCollection(ArrayList::new));

            if (tiedObjectivePointsPlayers.size() > 1) {
                //TODO: stampare/inviare "I vincitori sono..." + tutta questa lista di players
            } else {
                //TODO: stampare/inviare "Il vincitore è..." + il giocatore (unico) getFirst().
            }
        }

        //TODO: here we should destroy the file with the saved serialized data

        for(var player : GAME.getPlayers())
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

        // Sending lobbies list to players who were in this game (because they didn't have it updated)
        for(var target : returnLobby.getPlayers()){
            keyReverseLookup(ServerController.players, target::equals)
                    .serverMessage(
                            varargsToArrayList(
                                    "setLobbies",
                                    ServerController.lobbiesAndGames.entrySet().stream()
                                            .filter((entry) -> !(entry.getValue() instanceof Game))
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                            )
                    );
        }

        // Update lobbies' lists of all other active players
        for(var client : ServerController.players.keySet())
            if(!(ServerController.players.get(client) instanceof InGamePlayer))
                client.serverMessage(varargsToArrayList("updateLobby", lobbyUUID, returnLobby.generateDTO())); //updateLobby();
    }
}
