package it.polimi.ingsw.gc12.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameLobbyTest {

    Player player1;
    GameLobby lobby;
    Player player2;

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("Sacri");
        lobby = new GameLobby(player1, 2);
        player2 = new Player("Piants");
    }

    @Test
    void addPlayerSuccces() {
        lobby.addPlayer(player2);
        assert (lobby.getPlayers().contains(player2));
    }

    @Test
    void addPlayerNotSucccesForMaxNumberOfPlayer() {
        lobby.addPlayer(player2);
        assertEquals(true, lobby.getPlayers().contains(player2));
    }

    @Test
    void removePlayer() {
        lobby.addPlayer(player2);
        assertEquals(2, lobby.getPlayers().size());
        lobby.removePlayer(player1);
        assertEquals(1, lobby.getPlayers().size());
    }
}