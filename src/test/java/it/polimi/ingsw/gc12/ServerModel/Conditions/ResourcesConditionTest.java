package it.polimi.ingsw.gc12.ServerModel.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourcesConditionTest {

    @Test
    void numberOfTimesSatisfiedRed() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.MUSHROOM, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(8);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(0);
        ResourceCard c2 = (ResourceCard) array.get(3);
        ResourceCard c3 = (ResourceCard) array.get(5);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);

        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 2), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(3, 3), c3, Side.FRONT);

        assertEquals(1, c.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));



    }

    @Test
    void numberOfTimesSatisfiedGreen() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.GRASS, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(9);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(10);
        ResourceCard c2 = (ResourceCard) array.get(12);
        ResourceCard c3 = (ResourceCard) array.get(11);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);

        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.BACK);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT);

        assertEquals(2, c.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void numberOfTimesSatisfiedBlue() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.WOLF, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(10);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(20);
        ResourceCard c2 = (ResourceCard) array.get(21);
        ResourceCard c3 = (ResourceCard) array.get(24);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);

        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 2), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(3, 3), c3, Side.FRONT);

        assertEquals(1, c.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void numberOfTimesSatisfiedPurple() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        HashMap<Resource, Integer> cond = new HashMap<>();
        cond.put(Resource.BUTTERFLY, 3);
        ResourcesCondition c = new ResourcesCondition(cond);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(11);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(30);
        ResourceCard c2 = (ResourceCard) array.get(32);
        ResourceCard c3 = (ResourceCard) array.get(33);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);

        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.BACK);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT);

        assertEquals(2, c.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }


}