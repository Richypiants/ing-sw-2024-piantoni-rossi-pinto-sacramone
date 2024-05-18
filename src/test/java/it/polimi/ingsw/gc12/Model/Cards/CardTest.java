package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CardTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    Player player1;
    Player player2;
    GameLobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        });
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
    }

    @BeforeEach
    void setGameParameters() {

        player1 = new Player("Sacri");
        player2 = new Player("Piants");
        lobby = new GameLobby(player1, 2);
        game = new Game(lobby);
    }

    @Test
    void awardPoints() throws Throwable {
        game.getPlayers().get(0).addCardToHand(initialCards.get(0));
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), initialCards.get(0), Side.BACK);

        assertEquals(0, goldCards.get(0).awardPoints(game.getPlayers().get(0))); // don't touch this line

        game.getPlayers().get(0).addCardToHand(resourceCards.get(0));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(2));
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), resourceCards.get(0), Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT);

        game.getPlayers().get(0).addCardToHand(resourceCards.get(10));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(21));
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT);

        game.getPlayers().get(0).addCardToHand(goldCards.get(7));
        game.getPlayers().get(0).addCardToHand(goldCards.get(6));
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 3), goldCards.get(6), Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(3, 1), goldCards.get(7), Side.FRONT);

        game.getPlayers().get(0).addCardToHand(goldCards.get(0));
        game.getPlayers().get(0).placeCard(new GenericPair<>(4, 2), goldCards.get(0), Side.FRONT);

        assertEquals(2, goldCards.get(0).awardPoints(game.getPlayers().get(0)));
    }

    @Test
    void toStringTest() {
        ArrayList<ResourceCard> resourceCards;
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        assertInstanceOf(String.class, resourceCards.getFirst().toString());

    }

    @Test
    void toStringTestForInitial() {
        ArrayList<InitialCard> initialCards;
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
        assertInstanceOf(String.class, initialCards.getFirst().toString());

    }
}