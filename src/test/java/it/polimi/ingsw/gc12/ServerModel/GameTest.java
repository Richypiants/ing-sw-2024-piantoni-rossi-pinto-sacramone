package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        GameLobby lobby = new GameLobby(2, p1);
        lobby.addPlayer(p2);
        Game game = new Game(lobby);
        game.getCurrentState().nextPlayer();
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());
    }

    @Test
    void getCurrentPlayer() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());


    }

    @Test
    void drawFromCorrect() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getResourceCardsDeck()));



    }

    @Test
    void drawFromVisibleCards() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        assertInstanceOf(ResourceCard.class, game.drawFromVisibleCards(game.getPlacedResources(), 0));
    }
}