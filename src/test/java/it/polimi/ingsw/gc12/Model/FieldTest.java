package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;

    Player player;
    InGamePlayer playerGame;
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
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });
    }

    @BeforeEach
    void setGameParameters() {

        player = new Player("Sacri");
        playerGame = new InGamePlayer(player);

        lobby = new GameLobby(player, 1);
        game = new Game(lobby);
    }

    @Test
    void getCardCoordinates() throws Throwable {
        Field field = new Field();

        field.addCard(new GenericPair<>(0, 0), initialCards.get(0), Side.BACK);

        field.addCard(new GenericPair<>(1, -1), resourceCards.get(0), Side.FRONT);
        field.addCard(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT);

        playerGame.addCardToHand(resourceCards.get(10));
        playerGame.addCardToHand(resourceCards.get(21));
        field.addCard(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT);
        field.addCard(new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT);

        assertEquals(new GenericPair<>(0, 0), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(0, 0)).getX()));
        assertEquals(new GenericPair<>(1, -1), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(1, -1)).getX()));
        assertEquals(new GenericPair<>(-1, 1), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(-1, 1)).getX()));
        assertEquals(new GenericPair<>(2, 0), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(2, 0)).getX()));
        assertEquals(new GenericPair<>(-2, 2), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(-2, 2)).getX()));
    }
}