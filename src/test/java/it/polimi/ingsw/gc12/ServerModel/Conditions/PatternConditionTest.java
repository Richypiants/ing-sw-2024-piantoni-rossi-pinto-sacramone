package it.polimi.ingsw.gc12.ServerModel.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO: think about all possible fails and tests

class PatternConditionTest {

    @Test
    void genericPatternTest() {
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, 1, Resource.WOLF);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);
        ObjectiveCard c_o = new ObjectiveCard(3, 1, null, null, p);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, back);
        ResourceCard c2 = new ResourceCard(2, 1, null, null, corner, back);
        ResourceCard c3 = new ResourceCard(3, 0, null, null, corner, back);
        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 2), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));
    }


    @Test
    void DiagonalRedPattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(1, 1, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(2, 2, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(0);
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


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void DiagonalGreenPattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(-1, 1, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-2, 2, Resource.GRASS);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(1);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(10);
        ResourceCard c2 = (ResourceCard) array.get(11);
        ResourceCard c3 = (ResourceCard) array.get(12);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void DiagonalBluePattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));


        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(1, 1, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(2, 2, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(2);
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


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void DiagonalPurplePattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(-1, 1, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-2, 2, Resource.BUTTERFLY);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(3);
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
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void LredGreenPattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, -3, Resource.GRASS);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(4);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(0);
        ResourceCard c2 = (ResourceCard) array.get(2);
        ResourceCard c3 = (ResourceCard) array.get(12);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, -2), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void LgreenPurplePattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, -3, Resource.BUTTERFLY);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(5);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(10);
        ResourceCard c2 = (ResourceCard) array.get(12);
        ResourceCard c3 = (ResourceCard) array.get(30);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, -2), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void LblueRedPattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, 1, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(6);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(20);
        ResourceCard c2 = (ResourceCard) array.get(22);
        ResourceCard c3 = (ResourceCard) array.get(3);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 2), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void LPurplebluePattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(6);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(30);
        ResourceCard c2 = (ResourceCard) array.get(32);
        ResourceCard c3 = (ResourceCard) array.get(22);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 2), c3, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void multipattern() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(6);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(30);
        ResourceCard c2 = (ResourceCard) array.get(32);
        ResourceCard c3 = (ResourceCard) array.get(22);
        ResourceCard c4 = (ResourceCard) array.get(30);
        ResourceCard c5 = (ResourceCard) array.get(32);
        ResourceCard c6 = (ResourceCard) array.get(22);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).addCardToHand(c4);
        game.getPlayers().get(0).addCardToHand(c5);
        game.getPlayers().get(0).addCardToHand(c6);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 2), c3, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), c4, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, -1), c5, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), c6, Side.FRONT);


        assertEquals(2, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));


    }

    @Test
    void patternmix() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<>();
        Array.add(T1);
        Array.add(T2);
        Array.add(T3);
        PatternCondition p = new PatternCondition(Array);

        Triplet<Integer, Integer, Resource> T4 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T5 = new Triplet<>(1, 1, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T6 = new Triplet<>(2, 2, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> Array1 = new ArrayList<>();
        Array1.add(T4);
        Array1.add(T5);
        Array1.add(T6);
        PatternCondition p_1 = new PatternCondition(Array1);

        ObjectiveCard c_o = (ObjectiveCard) array1.get(6);
        ObjectiveCard c_o1 = (ObjectiveCard) array1.get(0);
        InitialCard c0 = (InitialCard) array2.get(0);
        ResourceCard c1 = (ResourceCard) array.get(30);
        ResourceCard c2 = (ResourceCard) array.get(32);
        ResourceCard c3 = (ResourceCard) array.get(22);

        ResourceCard c4 = (ResourceCard) array.get(0);
        ResourceCard c5 = (ResourceCard) array.get(3);
        ResourceCard c6 = (ResourceCard) array.get(5);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(1, p1);
        Game game = new Game(lobby);
        game.getPlayers().get(0).addCardToHand(c0);
        game.getPlayers().get(0).addCardToHand(c1);
        game.getPlayers().get(0).addCardToHand(c2);
        game.getPlayers().get(0).addCardToHand(c3);
        game.getPlayers().get(0).addCardToHand(c4);
        game.getPlayers().get(0).addCardToHand(c5);
        game.getPlayers().get(0).addCardToHand(c6);
        game.getPlayers().get(0).placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-1, -1), c2, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(-2, 2), c3, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(1, 1), c4, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(2, 2), c5, Side.FRONT);
        game.getPlayers().get(0).placeCard(new GenericPair<>(3, 3), c6, Side.FRONT);


        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().get(0)));
        assertEquals(1, p_1.numberOfTimesSatisfied(c_o1, game.getPlayers().get(0)));


    }
}