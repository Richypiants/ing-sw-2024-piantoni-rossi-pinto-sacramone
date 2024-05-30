package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.CardDeckTest;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<InitialCard> initialCards;

    Player player;
    InGamePlayer targetPlayer;
    Lobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
    }

    @BeforeEach
    void setGameParameters() {
        player = new Player("testPlayer");
        targetPlayer = new InGamePlayer(player);
    }

    @Test
    void getCardCoordinates(){
        //Preliminary operations to run the actual test
        Field field = new Field();

        assertDoesNotThrow(() -> field.addCard(new GenericPair<>(0, 0), initialCards.getFirst(), Side.BACK));
        assertDoesNotThrow(() -> field.addCard(new GenericPair<>(1, -1), resourceCards.getFirst(), Side.FRONT));
        assertDoesNotThrow(() -> field.addCard(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT));

        targetPlayer.addCardToHand(resourceCards.get(10));
        targetPlayer.addCardToHand(resourceCards.get(21));
        assertDoesNotThrow(() -> field.addCard(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT));
        assertDoesNotThrow(() -> field.addCard(new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT));

        //Actual test
        assertEquals(new GenericPair<>(0, 0), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(0, 0)).getX()));
        assertEquals(new GenericPair<>(1, -1), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(1, -1)).getX()));
        assertEquals(new GenericPair<>(-1, 1), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(-1, 1)).getX()));
        assertEquals(new GenericPair<>(2, 0), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(2, 0)).getX()));
        assertEquals(new GenericPair<>(-2, 2), field.getCardCoordinates(field.getPlacedCards().get(new GenericPair<>(-2, 2)).getX()));
    }

    @Test
    void addCardAtInvalidCoordinates(){
        Field field = new Field();

        assertThrows(InvalidCardPositionException.class, () -> field.addCard(new GenericPair<>(1,1), initialCards.getFirst(), Side.FRONT));
    }

    @Test
    void addCardAtValidCoordinates(){
        Field field = new Field();
        PlayableCard placedCard = initialCards.getFirst();

        assertDoesNotThrow(() -> field.addCard(new GenericPair<>(0,0), placedCard, Side.FRONT));
        assertNotNull(field.getPlacedCards().get(new GenericPair<>(0,0)));
        assertEquals(placedCard, field.getPlacedCards().get(new GenericPair<>(0,0)).getX());
    }
}