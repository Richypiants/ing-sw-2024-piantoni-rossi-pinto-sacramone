package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    Player player1;
    Lobby lobby;
    Player player2;
    Player player3;

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("Sacri");
        lobby = new Lobby(UUID.randomUUID(), player1, 2);
        player2 = new Player("Piants");
        player3 = new Player("TheSp");
    }

    @Test
    void addPlayerSuccessful() {
        assertDoesNotThrow(() -> lobby.addPlayer(player2));
        assert (lobby.getPlayers().contains(player2));
    }

    @Test
    void addPlayerNotSuccessfulForMaxNumberOfPlayers() {
        try {
            lobby.addPlayer(player2);
        } catch (FullLobbyException ignored) {
        }
        assertThrows(FullLobbyException.class, () -> lobby.addPlayer(player3));
    }

    @Test
    void removePlayer() {
        assertDoesNotThrow(() -> lobby.addPlayer(player2));
        assertEquals(2, lobby.getPlayers().size());
        lobby.removePlayer(player1);
        assertEquals(1, lobby.getPlayers().size());
    }

    @Test
    void successfulSetMaxPlayers() {
        Lobby lobby = new Lobby(UUID.randomUUID(), player1, 2);
        lobby.setMaxPlayers(4);
        assertEquals(4, lobby.getMaxPlayers());

    }

    @Test
    void unsuccessfulSetMaxPlayers() {
        int initialMaxPlayers = 2;
        Lobby lobby = new Lobby(UUID.randomUUID(), player1, initialMaxPlayers);
        lobby.setMaxPlayers(10); //Any value greater than 4
        assertEquals(initialMaxPlayers, lobby.getMaxPlayers());
        lobby.setMaxPlayers(0);
        assertEquals(initialMaxPlayers, lobby.getMaxPlayers());
    }

    @Test
    void operationsOnColorAssignment() {
        Lobby lobby = new Lobby(UUID.randomUUID(), player1, 2);
        Color firstColorChoice = Color.GREEN;
        Color secondColorChoice = Color.RED;
        assertEquals(Color.NO_COLOR, player1.getColor());


        assertDoesNotThrow( () -> lobby.assignColor(player1, firstColorChoice));
        assertEquals(firstColorChoice, player1.getColor());

        assertDoesNotThrow( () -> lobby.assignColor(player1, secondColorChoice));
        assertEquals(secondColorChoice, player1.getColor());

        assertDoesNotThrow( () -> lobby.addPlayer(player2));
        assertThrows(UnavailableColorException.class, () -> lobby.assignColor(player2, Color.BLACK));
        assertThrows(UnavailableColorException.class, () -> lobby.assignColor(player2, Color.RED));
        assertThrows(UnavailableColorException.class, () -> lobby.assignColor(player2, Color.NO_COLOR));
    }

    @Test
    void toStringTest() {
        lobby = new Lobby(UUID.randomUUID(), player1, 2);
        assertEquals("Lobby{" + "maxPlayers=" + 2 + " players=[" + "[Sacri, NO_COLOR]" +
                "] availableColors=" + "[RED, YELLOW, GREEN, BLUE" + "]}", lobby.toString());
    }

    @Test
    void setLobbyWithListTest() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        lobby = new Lobby(UUID.randomUUID(), 2, players);
        assert (lobby.getPlayers().contains(player1) && lobby.getPlayers().contains(player2));
        assertEquals(2, lobby.getPlayersNumber());
        assertEquals(2, lobby.getMaxPlayers());

    }


}