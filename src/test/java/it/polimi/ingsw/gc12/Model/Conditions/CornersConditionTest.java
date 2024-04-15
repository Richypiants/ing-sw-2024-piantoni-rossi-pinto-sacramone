package it.polimi.ingsw.gc12.Model.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
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

class CornersConditionTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;

    Player player;
    GameLobby lobby;
    Game game;
    CornersCondition corner;

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
        lobby = new GameLobby(player, 1);
        game = new Game(lobby);
        corner = new CornersCondition();
    }


    @Test
    void numberOfTimesSatisfied_1() throws Throwable {

        game.getPlayers().get(0).addCardToHand(initialCards.get(0));
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), initialCards.get(0), Side.BACK);
        assertEquals(0, corner.numberOfTimesSatisfied(initialCards.get(0), game.getPlayers().get(0)));

        game.getPlayers().get(0).addCardToHand(resourceCards.get(0));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(2));
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), resourceCards.get(0), Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT);

        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(0), game.getPlayers().get(0)));
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(2), game.getPlayers().get(0)));

        game.getPlayers().get(0).addCardToHand(resourceCards.get(10));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(21));
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT);

        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(10), game.getPlayers().get(0)));
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(21), game.getPlayers().get(0)));

        game.getPlayers().get(0).addCardToHand(goldCards.get(7));
        game.getPlayers().get(0).addCardToHand(goldCards.get(6));
        game.getPlayers().get(0).placeCard(new GenericPair<>(3, 1), goldCards.get(7), Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 3), goldCards.get(6), Side.FRONT);

        assertEquals(1, corner.numberOfTimesSatisfied(goldCards.get(7), game.getPlayers().get(0)));
        assertEquals(1, corner.numberOfTimesSatisfied(goldCards.get(6), game.getPlayers().get(0)));

        // Final check for initial card
        assertEquals(2, corner.numberOfTimesSatisfied(initialCards.get(0), game.getPlayers().get(0)));
    }

    @Test
    void numberOfTimesSatisfied_2() throws Throwable {

        game.getPlayers().get(0).addCardToHand(initialCards.get(0));
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), game.getPlayers().get(0).getCardsInHand().getFirst(), Side.BACK);
        assertEquals(0, corner.numberOfTimesSatisfied(initialCards.get(0), game.getPlayers().get(0)));

        game.getPlayers().get(0).addCardToHand(resourceCards.get(0));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(1));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(2));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(3));

        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(0), game.getPlayers().get(0)));

        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 2), resourceCards.get(2), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(2), game.getPlayers().get(0)));

        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), resourceCards.get(1), Side.FRONT);
        assertEquals(2, corner.numberOfTimesSatisfied(resourceCards.get(1), game.getPlayers().get(0)));

        // Semi-final check for initial card
        assertEquals(2, corner.numberOfTimesSatisfied(initialCards.get(0), game.getPlayers().get(0)));

        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, -1), resourceCards.get(3), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(3), game.getPlayers().get(0)));

        // Final check for initial card
        assertEquals(3, corner.numberOfTimesSatisfied(initialCards.get(0), game.getPlayers().get(0)));
    }

    @Test
    void numberOfTimesSatisfied_3() throws Throwable {

        game.getPlayers().get(0).addCardToHand(initialCards.get(0));
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), game.getPlayers().get(0).getCardsInHand().getFirst(), Side.BACK);

        game.getPlayers().get(0).addCardToHand(resourceCards.get(0));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(1));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(2));
        game.getPlayers().get(0).addCardToHand(resourceCards.get(3));

        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(0), game.getPlayers().get(0)));

        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), resourceCards.get(1), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(1), game.getPlayers().get(0)));

        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(2), game.getPlayers().get(0)));

        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, -1), resourceCards.get(3), Side.FRONT);
        assertEquals(1, corner.numberOfTimesSatisfied(resourceCards.get(1), game.getPlayers().get(0)));

        // Final check for initial card
        assertEquals(4, corner.numberOfTimesSatisfied(initialCards.get(0), game.getPlayers().get(0)));
    }
}