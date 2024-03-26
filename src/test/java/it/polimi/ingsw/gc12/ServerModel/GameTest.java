package it.polimi.ingsw.gc12.ServerModel;

import org.junit.jupiter.api.Test;

class GameTest {

    //I only need this for a moment, we will probably delete this (maybe not...)
    @Test
    void newGameCreation() {
        GameLobby lobby = new GameLobby(1, new Player("Piants"));
        Game game = new Game(lobby);
        System.out.println(game.getGoldCardsDeck().draw().getCenterBackResources());
    }

    @Test
    void nextPlayer() {

    }

    @Test
    void getCurrentPlayer() {


    }

    @Test
    void drawFrom() {


    }

    @Test
    void drawFromVisibleCards() {

    }
}