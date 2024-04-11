package it.polimi.ingsw.gc12.Model.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO: think about all possible fails and tests

class PatternConditionTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){});
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){});
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){});
    ArrayList<ObjectiveCard> objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){});

    @Test
    void genericPatternTest() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);
        
        ObjectiveCard c_o = objectiveCards.getFirst();
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);
        ResourceCard c2 = resourceCards.get(21);
        ResourceCard c3 = resourceCards.get(22);
        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.BACK);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, -1), c1, Side.BACK);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c2, Side.BACK);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c3, Side.BACK);

        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }


    @Test
    void DiagonalRedPattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(1, 1, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(2, 2, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.getFirst();
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

        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void DiagonalGreenPattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(-1, 1, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-2, 2, Resource.GRASS);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(1);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(10);
        ResourceCard c2 = resourceCards.get(11);
        ResourceCard c3 = resourceCards.get(12);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT);

        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void DiagonalBluePattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(1, 1, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(2, 2, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(2);
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
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), c3, Side.FRONT);

        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void DiagonalPurplePattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(-1, 1, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-2, 2, Resource.BUTTERFLY);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(3);
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
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-2, 2), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-3, 3), c3, Side.FRONT);
        
        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void LredGreenPattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, -3, Resource.GRASS);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(4);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(0);
        ResourceCard c2 = resourceCards.get(2);
        ResourceCard c3 = resourceCards.get(12);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, -2), c3, Side.FRONT);
        
        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void LgreenPurplePattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.GRASS);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, -3, Resource.BUTTERFLY);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(5);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(10);
        ResourceCard c2 = resourceCards.get(12);
        ResourceCard c3 = resourceCards.get(30);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, -2), c3, Side.FRONT);
        
        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void LblueRedPattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, 1, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(6);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);
        ResourceCard c2 = resourceCards.get(22);
        ResourceCard c3 = resourceCards.get(3);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c3, Side.FRONT);
        
        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void LPurplebluePattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(6);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(30);
        ResourceCard c2 = resourceCards.get(32);
        ResourceCard c3 = resourceCards.get(22);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 2), c3, Side.FRONT);
        
        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void multipattern() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(6);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(30);
        ResourceCard c2 = resourceCards.get(32);
        ResourceCard c3 = resourceCards.get(22);
        ResourceCard c4 = resourceCards.get(30);
        ResourceCard c5 = resourceCards.get(32);
        ResourceCard c6 = resourceCards.get(22);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().addCardToHand(c4);
        game.getPlayers().getFirst().addCardToHand(c5);
        game.getPlayers().getFirst().addCardToHand(c6);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, -1), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 2), c3, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, 1), c4, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, -1), c5, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-2, 2), c6, Side.FRONT);

        assertEquals(2, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void patternmix() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.BUTTERFLY);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(-1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> condition1 = new ArrayList<>();
        condition1.add(T1);
        condition1.add(T2);
        condition1.add(T3);
        PatternCondition p = new PatternCondition(condition1);

        Triplet<Integer, Integer, Resource> T4 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T5 = new Triplet<>(1, 1, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T6 = new Triplet<>(2, 2, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> condition2 = new ArrayList<>();
        condition2.add(T4);
        condition2.add(T5);
        condition2.add(T6);
        PatternCondition p_1 = new PatternCondition(condition2);

        ObjectiveCard c_o = objectiveCards.get(6);
        ObjectiveCard c_o1 = objectiveCards.get(0);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(30);
        ResourceCard c2 = resourceCards.get(32);
        ResourceCard c3 = resourceCards.get(22);

        ResourceCard c4 = resourceCards.get(0);
        ResourceCard c5 = resourceCards.get(3);
        ResourceCard c6 = resourceCards.get(5);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().addCardToHand(c4);
        game.getPlayers().getFirst().addCardToHand(c5);
        game.getPlayers().getFirst().addCardToHand(c6);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, 1), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-1, -1), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(-2, 2), c3, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c4, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c5, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), c6, Side.FRONT);

        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
        assertEquals(1, p_1.numberOfTimesSatisfied(c_o1, game.getPlayers().getFirst()));
    }

    @Test
    void intersectionsPatterns() throws Exception{
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, -3, Resource.GRASS);
        ArrayList<Triplet<Integer, Integer, Resource>> condition1 = new ArrayList<>();
        condition1.add(T1);
        condition1.add(T2);
        condition1.add(T3);
        PatternCondition p = new PatternCondition(condition1);

        Triplet<Integer, Integer, Resource> T4 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T5 = new Triplet<>(1, 1, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T6 = new Triplet<>(2, 2, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> condition2 = new ArrayList<>();
        condition2.add(T4);
        condition2.add(T5);
        condition2.add(T6);
        PatternCondition p_1 = new PatternCondition(condition2);

        ObjectiveCard c_o = objectiveCards.get(4);
        ObjectiveCard c_o1 = objectiveCards.get(0);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(2);
        ResourceCard c2 = resourceCards.get(10);
        ResourceCard c4 = resourceCards.get(3);
        ResourceCard c5 = resourceCards.get(6);
        ResourceCard c6 = resourceCards.get(5);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c4);
        game.getPlayers().getFirst().addCardToHand(c5);
        game.getPlayers().getFirst().addCardToHand(c6);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c4, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c5, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), c6, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 3), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 0), c2, Side.FRONT);
        
        assertEquals(1, p.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
        assertEquals(1, p_1.numberOfTimesSatisfied(c_o1, game.getPlayers().getFirst()));
    }

    @Test
    void twoDiagonalInRow() throws Exception{
        //recognizes correctly 3 pattern
        Triplet<Integer, Integer, Resource> T4 = new Triplet<>(0, 0, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T5 = new Triplet<>(1, 1, Resource.MUSHROOM);
        Triplet<Integer, Integer, Resource> T6 = new Triplet<>(2, 2, Resource.MUSHROOM);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T4);
        condition.add(T5);
        condition.add(T6);
        PatternCondition p_1 = new PatternCondition(condition);

        ObjectiveCard c_o = objectiveCards.get(6);
        ObjectiveCard c_o1 = objectiveCards.get(0);
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(0);
        ResourceCard c2 = resourceCards.get(3);
        ResourceCard c3 = resourceCards.get(6);

        ResourceCard c4 = resourceCards.get(0);
        ResourceCard c5 = resourceCards.get(3);
        ResourceCard c6 = resourceCards.get(6);

        Player p1 = new Player("giovanni");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);
        game.getPlayers().getFirst().addCardToHand(c0);
        game.getPlayers().getFirst().addCardToHand(c1);
        game.getPlayers().getFirst().addCardToHand(c2);
        game.getPlayers().getFirst().addCardToHand(c3);
        game.getPlayers().getFirst().addCardToHand(c4);
        game.getPlayers().getFirst().addCardToHand(c5);
        game.getPlayers().getFirst().addCardToHand(c6);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), c4, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), c5, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), c6, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(4, 4), c1, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(5, 5), c2, Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(6, 6), c3, Side.FRONT);

        assertEquals(3, p_1.numberOfTimesSatisfied(c_o1, game.getPlayers().getFirst()));
    }
}