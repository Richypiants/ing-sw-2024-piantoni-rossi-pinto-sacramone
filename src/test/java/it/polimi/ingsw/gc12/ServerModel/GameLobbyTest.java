package it.polimi.ingsw.gc12.ServerModel;

import org.junit.jupiter.api.Test;

class GameLobbyTest {

    @Test
    void addPlayerSuccces() {
        Player player = new Player("test");
        GameLobby lobby = new GameLobby(2, player);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        assert (lobby.getListOfPlayers().contains(player1));
    }

    @Test
    void addPlayerNotSucccesForMaxNumberOfPlayer() {
        Player player = new Player("test");
        GameLobby lobby = new GameLobby(1, player);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        assert (!lobby.getListOfPlayers().contains(player1));
    }

    @Test
    void removePlayer() {
        Player player = new Player("test");
        GameLobby lobby = new GameLobby(2, player);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        assert (lobby.getListOfPlayers().contains(player1));
        lobby.removePlayer(player1);
        assert (!lobby.getListOfPlayers().contains(player1));


    }
}