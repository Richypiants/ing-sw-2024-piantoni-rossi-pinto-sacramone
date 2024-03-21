package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldTest {

    @Test
    void cardUndefined() {  // addCard()

        Resource[][] corner = {{Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF},
                {Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF}};

        Triplet<Integer, Integer, Resource> T1 = new Triplet<Integer, Integer, Resource>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<Integer, Integer, Resource>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<Integer, Integer, Resource>(1, 1, Resource.WOLF);

        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<Triplet<Integer, Integer, Resource>>();

        Array.add(T1);
        Array.add(T2);
        Array.add(T3);

        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = new ObjectiveCard(3, 1, null, null, p);

        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, new EnumMap<>(Resource.class));
        ResourceCard c2 = new ResourceCard(2, 1, null, null, corner, new EnumMap<>(Resource.class));
        ResourceCard c3 = new ResourceCard(3, 0, null, null, corner, new EnumMap<>(Resource.class));

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.placeCard(c1, Side.FRONT, new GenericPair<>(0, 0));
        p1_g.placeCard(c2, Side.FRONT, new GenericPair<>(0, -2));
        p1_g.placeCard(c3, Side.FRONT, new GenericPair<>(1, 1));

        GenericPair<Integer, Integer> coo = new GenericPair<>(0, 0);

        Resource[][] corners = new Resource[2][4];
        EnumMap<Resource, Integer> centerBackResources = new EnumMap<>(Resource.class);

        PlayableCard card = new ResourceCard(10, 0, null, null, corners, centerBackResources);

        assertEquals(false, p1_g.getOwnField().addCard(coo, null, Side.BACK));

        // ERRORE:
        // Il test da come risultato "false" ma dovrebbe restituire una nullPointerException dato che
        // addCard cerca di istanziare un GenericPair con all'interno un valore "null".
    }

    @Test
    void getCardCoordinates() {

        Resource[][] corner = {{Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF},
                {Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF}};

        Triplet<Integer, Integer, Resource> T1 = new Triplet<Integer, Integer, Resource>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<Integer, Integer, Resource>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<Integer, Integer, Resource>(1, 1, Resource.WOLF);

        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<Triplet<Integer, Integer, Resource>>();

        Array.add(T1);
        Array.add(T2);
        Array.add(T3);

        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = new ObjectiveCard(3, 1, null, null, p);

        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, new EnumMap<>(Resource.class));
        ResourceCard c2 = new ResourceCard(2, 1, null, null, corner, new EnumMap<>(Resource.class));
        ResourceCard c3 = new ResourceCard(3, 0, null, null, corner, new EnumMap<>(Resource.class));

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.placeCard(c1, Side.FRONT, new GenericPair<>(0, 0));
        p1_g.placeCard(c2, Side.FRONT, new GenericPair<>(0, -2));
        p1_g.placeCard(c3, Side.FRONT, new GenericPair<>(1, 1));

        System.out.println(p1_g.getPlacedCards().keySet());

        GenericPair<Integer, Integer> result = p1_g.getOwnField().getCardCoordinates(c1);

        GenericPair<Integer, Integer> expected = new GenericPair<>(0, 0);

        assertEquals(expected, result);

        //TODO: RIVEDERE
    }
}