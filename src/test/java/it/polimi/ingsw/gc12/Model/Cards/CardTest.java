package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
    });
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
    });
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
    });

    @Test
    void awardPoints() throws Throwable {

        Player player1 = new Player("Sacri");
        Player player2 = new Player("Piants");

        GameLobby lobby = new GameLobby(player1, 2);
        Game game = new Game(lobby);

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
}