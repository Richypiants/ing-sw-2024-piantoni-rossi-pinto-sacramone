package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
    });
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
    });
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
    });

    //I only need this for a moment, we will probably delete this (maybe not...)
    @Test
    void newGameCreation() {
        GameLobby lobby = new GameLobby(new Player("Piants"), 1);
        Game game = new Game(lobby);
        System.out.println(game.getGoldCardsDeck().draw().getCenterBackResources());
    }

    @Test
    void nextPlayer() {
        Player player1 = new Player("Sacri");
        Player player2 = new Player("Piants");

        GameLobby lobby = new GameLobby(player1, 2);
        lobby.addPlayer(player2);
        Game game = new Game(lobby);

        game.getCurrentState().nextPlayer();  // don't touch this line
        assertEquals(game.getPlayers().get(0), game.getCurrentPlayer());
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
    void emptyDeck() throws Throwable {

        Player player = new Player("Sacri");

        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);

        for (int i = 0; i < 38; i++) {
            game.drawFrom(game.getResourceCardsDeck());
        }

        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getResourceCardsDeck()));

        for (int i = 0; i < 38; i++) {
            game.drawFrom(game.getGoldCardsDeck());
        }

        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getGoldCardsDeck()));


    }

    @Test
    void emptyVisibleCardArrays() throws Throwable {

        Player player = new Player("Sacri");

        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);

        for (int i = 0; i < 39; i++) {
            game.drawFrom(game.getPlacedGolds(), 0);
        }

        game.drawFrom(game.getPlacedGolds(), 1);

        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedGolds(), 0));
    }
}