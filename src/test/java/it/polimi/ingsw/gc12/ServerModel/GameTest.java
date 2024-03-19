package it.polimi.ingsw.gc12.ServerModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

    @Test
    void nextPlayer() {

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);
        GameLobby lobby = new GameLobby(4, player);
        Game game = new Game(lobby);

        game.nextPlayer();
        assertEquals(game.getCurrentPlayer(), 1);

    }

    @Test
    void getCurrentPlayer() {

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);
        GameLobby lobby = new GameLobby(4, player);
        Game game = new Game(lobby);

        assertEquals(game.getCurrentPlayer(), 0);

        game.nextPlayer();
        assertEquals(game.getCurrentPlayer(), 1);

        game.nextPlayer();
        assertEquals(game.getCurrentPlayer(), 2);

    }

    @Test
    void drawFrom() {


    }

    @Test
    void drawFromVisibleCards() {

    }
}