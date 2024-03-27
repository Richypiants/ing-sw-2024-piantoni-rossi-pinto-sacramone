package it.polimi.ingsw.gc12.ServerModel;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Cards.CardDeck;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CardDeckTest {

    @Test
    void deckCorrectSize() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        assertEquals(40, array.size());
    }

    @Test
    void deckIsNotEmpty() {
        CardDeck<ResourceCard> deck = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        assert (!deck.isEmpty());

    }

    @Test
    void correctDraw() {
        Player p1 = new Player("giovanni");
        InGamePlayer p1_g = new InGamePlayer(p1);
        CardDeck<ResourceCard> deck = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));

        p1_g.addCardToHand(deck.draw());
        assert (!p1_g.getCardsInHand().isEmpty());
    }

    @Test
    void fullDeckPull() {
        CardDeck<ResourceCard> deck = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));

        while (!deck.isEmpty()) {
            assertInstanceOf(ResourceCard.class, deck.draw());
        }

        assertTrue(deck.isEmpty());


    }
}