package it.polimi.ingsw.gc12.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameLobbyTest {

    @Test
    void addPlayerSuccces() {
        Player player = new Player("test");
        GameLobby lobby = new GameLobby(player, 2);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        assert (lobby.getPlayers().contains(player1));
    }

    @Test
    void addPlayerNotSucccesForMaxNumberOfPlayer() {
        Player player = new Player("test");
        GameLobby lobby = new GameLobby(player, 1);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        assert (!lobby.getPlayers().contains(player1));
    }

    @Test
    void removePlayer() {
        Player player1 = new Player("Sacri");
        GameLobby lobby = new GameLobby(player1, 2);
        Player player2 = new Player("Piants");
        lobby.addPlayer(player2);
        assertEquals(2, lobby.getPlayers().size());
        lobby.removePlayer(player1);
        assertEquals(1, lobby.getPlayers().size());
    }
}