package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InGamePlayerTest {

    @Test
    void placeCard() {

        Resource[][] corner = {{Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF},
                {Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF}};

        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, new EnumMap<>(Resource.class));

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        assertEquals(true, p1_g.placeCard(c1, Side.FRONT, new GenericPair<>(0, 0)));

        // TODO: WIP (Finire metodo addCard() in Field e testare di nuovo)

    }

    @Test
    void addCardToHand() {  // OK

        Resource[][] corner = {{Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF},
                {Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF}};

        Triplet<Integer, Integer, Resource> T1 = new Triplet<Integer, Integer, Resource>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<Integer, Integer, Resource>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<Integer, Integer, Resource>(1, 1, Resource.WOLF);

        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, new EnumMap<>(Resource.class));

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.addCardToHand(c1);

        boolean result = p1_g.getCardsInHand().contains(c1);

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

        Player p1 = new Player("SACRI");
        InGamePlayer p1_g = new InGamePlayer(p1);

        p1_g.setSecretObjective(c_o);

        assertEquals(c_o, p1_g.getSecretObjective());
    }
}