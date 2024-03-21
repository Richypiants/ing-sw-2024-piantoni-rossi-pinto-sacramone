package it.polimi.ingsw.gc12.ServerModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

    @Test
    void nextPlayer() {

        Player player = new Player("test");
        GameLobby lobby = new GameLobby(2, player);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        Game game = new Game(lobby);

        game.nextPlayer();
        assertEquals(player1, game.getCurrentPlayer());

    }

    @Test
    void getCurrentPlayer() {

        Player player = new Player("test");
        GameLobby lobby = new GameLobby(2, player);
        Player player1 = new Player("test1");
        lobby.addPlayer(player1);
        Game game = new Game(lobby);


        assertEquals(player, game.getCurrentPlayer());

    }

    @Test
    void drawFrom() {


    }

    @Test
    void drawFromVisibleCards() {

    }
}