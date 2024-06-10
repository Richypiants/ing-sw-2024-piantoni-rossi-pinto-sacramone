package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CardTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    Player player1;
    Player player2;
    Lobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        goldCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.GOLD_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
    }

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("testPlayer_1");
        player2 = new Player("testPlayer_2");
        lobby = new Lobby(UUID.randomUUID(), player1, 2);
        game = new Game(lobby);
    }

    @Test
    void awardPoints() throws Throwable {
        game.getPlayers().getFirst().addCardToHand(initialCards.getFirst());
        game.placeCard(game.getPlayers().getFirst(),new GenericPair<>(0, 0), initialCards.getFirst(), Side.BACK);

        assertEquals(0, goldCards.getFirst().awardPoints(game.getPlayers().getFirst()));

        game.getPlayers().getFirst().addCardToHand(resourceCards.get(0));
        game.getPlayers().getFirst().addCardToHand(resourceCards.get(2));
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(1, -1), resourceCards.get(0), Side.FRONT);
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT);

        game.getPlayers().getFirst().addCardToHand(resourceCards.get(10));
        game.getPlayers().getFirst().addCardToHand(resourceCards.get(21));
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT);
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT);

        game.getPlayers().getFirst().addCardToHand(goldCards.get(7));
        game.getPlayers().getFirst().addCardToHand(goldCards.get(6));
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(-1, 3), goldCards.get(6), Side.FRONT);
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(3, 1), goldCards.get(7), Side.FRONT);

        game.getPlayers().getFirst().addCardToHand(goldCards.get(0));
        game.placeCard(game.getPlayers().getFirst(), new GenericPair<>(4, 2), goldCards.get(0), Side.FRONT);

        assertEquals(2, goldCards.getFirst().awardPoints(game.getPlayers().getFirst()));
    }

    @Test
    void toStringTest() {
        assertInstanceOf(String.class, resourceCards.getFirst().toString());
    }

    @Test
    void toStringTestForInitial() {
        assertInstanceOf(String.class, initialCards.getFirst().toString());
    }

    @Test
    void cardConstructorTest(){
        int id = 100;
        int pointsGranted = 1;
        Card placeHolderCard = new ResourceCard(id, pointsGranted, new HashMap<>(), new HashMap<>());
        assertEquals(id, placeHolderCard.ID);
        assertEquals(pointsGranted, placeHolderCard.POINTS_GRANTED);
    }
}