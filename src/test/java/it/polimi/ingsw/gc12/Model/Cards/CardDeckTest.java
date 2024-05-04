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

class CardDeckTest {

    @Test
    void deckCorrectSize() {
        ArrayList<Card> resource = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(40, resource.size());

        ArrayList<Card> gold = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(40, gold.size());

        ArrayList<Card> initial = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(6, initial.size());

        ArrayList<Card> objective = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(16, objective.size());
    }

    @Test
    void deckIsNotEmpty() {
        CardDeck<ResourceCard> resource = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        assertEquals(false, resource.isEmpty());

        ArrayList<Card> gold = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(false, gold.isEmpty());

        ArrayList<Card> initial = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(false, initial.isEmpty());

        ArrayList<Card> objective = new ArrayList<>(Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        })));
        assertEquals(false, objective.isEmpty());

    }

    @Test
    void correctDraw() {
        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        CardDeck<ResourceCard> deck = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));

        assertDoesNotThrow(() -> game.getPlayers().getFirst().addCardToHand(deck.draw()));
        assert (!game.getPlayers().getFirst().getCardsInHand().isEmpty());
    }

    @Test
    void fullDeckPull() {
        CardDeck<ResourceCard> deck = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));

        while (!deck.isEmpty()) {
            assertDoesNotThrow(() -> assertInstanceOf(ResourceCard.class, deck.draw()));
        }

        assertTrue(deck.isEmpty());
    }
}