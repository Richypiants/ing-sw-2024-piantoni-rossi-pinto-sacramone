package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.GoldCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.ServerModel.Conditions.CornersCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.ResourcesCondition;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

class InGamePlayerTest {

    @Test
    void placeCard() {

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);

        EnumMap<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);

        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, back);

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        assertEquals(true, p1_g.placeCard(new GenericPair<>(0, 0), c1, Side.FRONT));



    }

    @Test
    void addCardToHand() {  // OK

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);

        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.addCardToHand(c0);

        boolean result = p1_g.getCardsInHand().contains(c0);

        assertEquals(true, result);


    }

    @Test
    void incrementOwnedResource() {  // OK

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.incrementOwnedResource(Resource.SCROLL, 2);

        assertEquals(2, p1_g.getOwnedResources().get(Resource.SCROLL));
    }

    @Test
    void setSecretObjective() {  // OK


        Triplet<Integer, Integer, Resource> T1 = new Triplet<Integer, Integer, Resource>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<Integer, Integer, Resource>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<Integer, Integer, Resource>(1, 1, Resource.WOLF);

        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<Triplet<Integer, Integer, Resource>>();

        Array.add(T1);
        Array.add(T2);
        Array.add(T3);

        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = new ObjectiveCard(3, 1, null, null, p);

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.setSecretObjective(c_o);

        assertEquals(c_o, p1_g.getSecretObjective());


    }

    @Test
    void ResourceRecalcTest() {
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, back);
        ResourceCard c2 = new ResourceCard(2, 1, null, null, corner, back);
        Player p1 = new Player("giovanni");
        InGamePlayer p1_g = new InGamePlayer(p1);
        p1_g.addCardToHand(c0);
        p1_g.addCardToHand(c1);
        p1_g.addCardToHand(c2);
        p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        p1_g.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT);
        p1_g.placeCard(new GenericPair<>(1, 1), c2, Side.FRONT);


        assertEquals(10, p1_g.getOwnedResources().get(Resource.WOLF));

    }

    @Test
    void CardNotInHandNotPlaced() {
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, back);

        Player p1 = new Player("giovanni");
        InGamePlayer p1_g = new InGamePlayer(p1);
        p1_g.addCardToHand(c0);

        p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertFalse(p1_g.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));
    }

    @Test
    void GoldNeededResourcesNotSat() {
        // TODO :Test run Correctly But should not for NumberOFTimesSatisfied , check in future
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        Map<Resource, Integer> Needed = new HashMap<>();
        Needed.put(Resource.WOLF, 10);
        PointsCondition p = new CornersCondition();
        ResourcesCondition RP = new ResourcesCondition(Needed);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        GoldCard c1 = new GoldCard(1, 2, null, null, corner, back, p, RP);

        Player p1 = new Player("giovanni");
        InGamePlayer p1_g = new InGamePlayer(p1);
        p1_g.addCardToHand(c0);
        p1_g.addCardToHand(c1);
        p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertFalse(p1_g.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));

    }
}