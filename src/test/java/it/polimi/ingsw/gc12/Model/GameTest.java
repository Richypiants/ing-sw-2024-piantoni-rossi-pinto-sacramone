package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
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
        Player player = new Player("player");
        Player p2 = new Player("P2");
        GameLobby lobby = new GameLobby(player, 2);
        lobby.addPlayer(p2);
        Game game = new Game(lobby);
        game.getCurrentState().nextPlayer();
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());
    }

    @Test
    void getCurrentPlayer() {
        Player player = new Player("player");
        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());
    }

    @Test
    void drawFromCorrect() throws EmptyDeckException {
        Player player = new Player("player");
        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getResourceCardsDeck()));
    }

    @Test
    void drawFromVisibleCardsResource() throws EmptyDeckException {
        Player player = new Player("player");
        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getPlacedResources(), 0));
    }

    @Test
    void drawFromVisibleCardsGold() throws EmptyDeckException {
        Player player = new Player("player");
        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);
        assertInstanceOf(GoldCard.class, game.drawFrom(game.getPlacedGolds(), 0));
    }

    @Test
    void emptyDeck() {
        // assertThrows;
    }

    @Test
    void emptyVisibleCardArrays() {
        // assertThrows;
    }
}