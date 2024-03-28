package it.polimi.ingsw.gc12.ServerModel.Conditions;

import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
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
    void patternWithNoTiles() {
        /*PatternCondition p = new PatternCondition(
                new ArrayList<Triplet<Integer, Integer, Resource>>()
        );

        //TODO: come back here after implementing ObjectiveCard and InGamePlayer
        assertThrows(p.numberOfTimesSatisfied(
                        //TODO: add exception here
                        new ObjectiveCard(0, 0, null, null, null),
                        new InGamePlayer("tester")
                )
        );*/
    }

    @Test
    void patternWithOnlyOneTile() {
        /*//TODO: come up with a good example
        PatternCondition p = new PatternCondition(
                new ArrayList<Triplet<Integer, Integer, Resource>>(x)
        );

        //TODO: come back here after implementing ObjectiveCard and InGamePlayer
        assertX(p.numberOfTimesSatisfied(
                        new ObjectiveCard(0, 0, null, null, null),
                        new InGamePlayer("tester")
                )
        );*/
    }

    @Test
    void patternWithMoreTiles() {
        /*//TODO: come up with a good example
        PatternCondition p = new PatternCondition(
                new ArrayList<Triplet<Integer, Integer, Resource>>()
        );

        //TODO: come back here after implementing ObjectiveCard and InGamePlayer
        assertX(p.numberOfTimesSatisfied(
                        new ObjectiveCard(0, 0, null, null, null),
                        new InGamePlayer("tester")
                )
        );*/
    }
}