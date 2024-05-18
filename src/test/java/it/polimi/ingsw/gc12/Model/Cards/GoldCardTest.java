package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
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

class GoldCardTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    Player player;
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

        player = new Player("Sacri");
        lobby = new GameLobby(player, 1);
        game = new Game(lobby);
    }

    @Test
    void awardPoints() throws Throwable {  // OK
        InGamePlayer playerGame = game.getPlayers().getFirst();

        playerGame.addCardToHand(initialCards.getFirst());
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK);

        playerGame.addCardToHand(resourceCards.get(4));
        playerGame.placeCard(new GenericPair<>(1, -1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        playerGame.addCardToHand(goldCards.get(30));
        playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(2, playerGame.getPlacedCards().get(new GenericPair<>(-1, 1)).getX().awardPoints(playerGame));
    }

    @Test
    void toStringTest() {
        assertInstanceOf(String.class, resourceCards.getFirst().toString());

    }
}