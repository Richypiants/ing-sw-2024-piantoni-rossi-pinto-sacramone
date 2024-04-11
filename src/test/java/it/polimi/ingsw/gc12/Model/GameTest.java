package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GameTest {

    //I only need this for a moment, we will probably delete this (maybe not...)
    @Test
    void newGameCreation() {
        GameLobby lobby = new GameLobby(new Player("Piants"), 1);
        Game game = new Game(lobby);
        System.out.println(game.getGoldCardsDeck().draw().getCenterBackResources());
    }

    @Test
    void nextPlayer() {
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        GameLobby lobby = new GameLobby(p1, 2);
        lobby.addPlayer(p2);
        Game game = new Game(lobby);
        game.getCurrentState().nextPlayer();
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());
    }

    @Test
    void getCurrentPlayer() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());
    }

    @Test
    void drawFromCorrect() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getResourceCardsDeck()));
    }

    @Test
    void drawFromVisibleCardsResource() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getPlacedResources(), 0));
    }

    @Test
    void drawFromVisibleCardsGold() {
        Player p1 = new Player("P1");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        assertInstanceOf(GoldCard.class, game.drawFrom(game.getPlacedGolds(), 0));
    }
}