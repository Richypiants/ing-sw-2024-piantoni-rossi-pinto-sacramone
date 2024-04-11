package it.polimi.ingsw.gc12.Model.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourcesConditionTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){});
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){});
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){});
    ArrayList<ObjectiveCard> objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){});

    @Test
    void numberOfTimesSatisfiedRed() throws Exception {
        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.MUSHROOM, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = objectiveCards.get(8);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(0);
        ResourceCard c2 = resourceCards.get(3);
        ResourceCard c3 = resourceCards.get(5);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);

        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), c3, Side.FRONT);

        assertEquals(1, c.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void numberOfTimesSatisfiedGreen(){
        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.GRASS, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = objectiveCards.get(9);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(10);
        ResourceCard c2 = resourceCards.get(12);
        ResourceCard c3 = resourceCards.get(11);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);

        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.BACK));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT));

        assertEquals(2, c.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void numberOfTimesSatisfiedBlue() {
        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.WOLF, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = objectiveCards.get(10);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);
        ResourceCard c2 = resourceCards.get(21);
        ResourceCard c3 = resourceCards.get(24);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);

        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c2, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), c3, Side.FRONT));

        assertEquals(1, c.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void numberOfTimesSatisfiedPurple() {
        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.BUTTERFLY, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = objectiveCards.get(11);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(30);
        ResourceCard c2 = resourceCards.get(32);
        ResourceCard c3 = resourceCards.get(33);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);

        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.BACK));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT));
        assertDoesNotThrow(() -> game.getPlayers().getFirst().placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT));

        assertEquals(2, c.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }


}