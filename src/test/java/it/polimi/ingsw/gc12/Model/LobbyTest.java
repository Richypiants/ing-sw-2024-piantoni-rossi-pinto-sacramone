package it.polimi.ingsw.gc12.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LobbyTest {

    Player player1;
    Lobby lobby;
    Player player2;

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("Sacri");
        lobby = new Lobby(player1, 2);
        player2 = new Player("Piants");
    }

    @Test
    void addPlayerSuccessful() {
        lobby.addPlayer(player2);
        assert (lobby.getPlayers().contains(player2));
    }

    @Test
    void addPlayerNotSuccessfulForMaxNumberOfPlayer() {
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

    @Test
    void setMaxPlayerTest() {
        Lobby lobby = new Lobby(player1, 2);
        lobby.setMaxPlayers(4);
        assertEquals(4, lobby.getMaxPlayers());
    }

    @Test
    void shufflePlayerTest() {
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        lobby.shufflePlayers();
        assert (lobby.getPlayers().contains(player2) && lobby.getPlayers().contains(player1));
    }

    @Test
    void toStringTest() {
        lobby = new Lobby(player1, 2);
        assertEquals("Lobby{" + "maxPlayers=" + 2 + " players=[" + "[Sacri, NO_COLOR]" +
                "] availableColors=" + "[RED, YELLOW, GREEN, BLUE" + "]}", lobby.toString());
    }

    @Test
    void setLobbyWithListTest() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        lobby = new Lobby(2, players);
        assert (lobby.getPlayers().contains(player1) && lobby.getPlayers().contains(player2));
        assertEquals(2, lobby.getPlayersNumber());
        assertEquals(2, lobby.getMaxPlayers());

    }


}