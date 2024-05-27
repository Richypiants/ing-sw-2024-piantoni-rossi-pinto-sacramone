package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CardDeckTest {
    final int RESOURCE_DECK_SIZE = 40;
    final int GOLD_DECK_SIZE = 40;
    final int INITIAL_DECK_SIZE = 6;
    final int OBJECTIVE_DECK_SIZE = 16;
    public final static String RESOURCE_DECK_FILENAME = "resource_cards.json";
    public final static String GOLD_DECK_FILENAME = "gold_cards.json";
    public final static String INITIAL_DECK_FILENAME = "initial_cards.json";
    public final static String OBJECTIVE_DECK_FILENAME = "objective_cards.json";

    protected static <T extends Card> CardDeck<T> loadCardDeck(String filename, TypeToken<ArrayList<T>> typifiedTypeToken){
        return new CardDeck<>(Objects.requireNonNull(
                JSONParser.deckFromJSONConstructor(
                        filename,
                        typifiedTypeToken
                )));
    }

    public static <T extends Card> ArrayList<T> loadCardDeckAsArrayList(String filename, TypeToken<ArrayList<T>> typifiedTypeToken){
        return new ArrayList<>(Objects.requireNonNull(
                JSONParser.deckFromJSONConstructor(
                        filename,
                        typifiedTypeToken
                )));
    }

    @Test
    void resourceDeckHasCorrectSize(){
        ArrayList<ResourceCard> resourceCards = loadCardDeckAsArrayList(RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        assertEquals(RESOURCE_DECK_SIZE, resourceCards.size());
    }

    @Test
    void goldDeckHasCorrectSize(){
        ArrayList<GoldCard> goldCards = loadCardDeckAsArrayList(GOLD_DECK_FILENAME, new TypeToken<>(){});
        assertEquals(GOLD_DECK_SIZE, goldCards.size());
    }

    @Test
    void initialDeckHasCorrectSize(){
        ArrayList<InitialCard> initialCards = loadCardDeckAsArrayList(INITIAL_DECK_FILENAME, new TypeToken<>(){});
        assertEquals(INITIAL_DECK_SIZE, initialCards.size());
    }

    @Test
    void objectiveDeckHasCorrectSize(){
        ArrayList<ObjectiveCard> objectiveCards = loadCardDeckAsArrayList(OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});
        assertEquals(OBJECTIVE_DECK_SIZE, objectiveCards.size());
    }

    @Test
    void cardDecksAreNotEmpty() {
        CardDeck<ResourceCard> resourceCardDeck = loadCardDeck(RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        CardDeck<ResourceCard> goldCardDeck = loadCardDeck(GOLD_DECK_FILENAME, new TypeToken<>(){});
        CardDeck<ResourceCard> initialCardDeck = loadCardDeck(INITIAL_DECK_FILENAME, new TypeToken<>(){});
        CardDeck<ResourceCard> objectiveCardDeck = loadCardDeck(OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});

        assertFalse(resourceCardDeck.isEmpty());
        assertFalse(goldCardDeck.isEmpty());
        assertFalse(initialCardDeck.isEmpty());
        assertFalse(objectiveCardDeck.isEmpty());
    }

    @Test
    void correctDraw() {
        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        CardDeck<ResourceCard> resourceCardDeck = loadCardDeck(RESOURCE_DECK_FILENAME, new TypeToken<>(){});

        assertDoesNotThrow(() -> game.getPlayers().getFirst().addCardToHand(resourceCardDeck.draw()));
        assert(!game.getPlayers().getFirst().getCardsInHand().isEmpty());
    }

    @Test
    void fullDeckPull() {
        CardDeck<ResourceCard> resourceCardDeck = loadCardDeck(RESOURCE_DECK_FILENAME, new TypeToken<>(){});

        while (!resourceCardDeck.isEmpty()) {
            assertDoesNotThrow(() -> assertInstanceOf(ResourceCard.class, resourceCardDeck.draw()));
        }

        assertTrue(resourceCardDeck.isEmpty());
    }

    @Test
    void peekFromNotEmptyDeck(){
        CardDeck<ResourceCard> resourceCardDeck = loadCardDeck(RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        assertInstanceOf(ResourceCard.class, resourceCardDeck.peek());
        assertNotNull(resourceCardDeck.peek());
    }

    @Test
    void peekFromEmptyDeck(){
        CardDeck<ResourceCard> resourceCardDeck = loadCardDeck(RESOURCE_DECK_FILENAME, new TypeToken<>(){});

        while (!resourceCardDeck.isEmpty())
            assertDoesNotThrow(resourceCardDeck::draw);

        assertNull(resourceCardDeck.peek());
    }
}