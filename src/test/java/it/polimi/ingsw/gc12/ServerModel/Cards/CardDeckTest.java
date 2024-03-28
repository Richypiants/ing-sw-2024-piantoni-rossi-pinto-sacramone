package it.polimi.ingsw.gc12.ServerModel.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.Player;
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
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        CardDeck<ResourceCard> deck = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));

        game.getPlayers().get(0).addCardToHand(deck.draw());
        assert (!game.getPlayers().get(0).getCardsInHand().isEmpty());
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