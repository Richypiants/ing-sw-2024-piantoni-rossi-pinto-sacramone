package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    @Test
    void awardPoints() {

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);

        ResourceCard c1 = new ResourceCard(1, 4, null, null, corner, new EnumMap<>(Resource.class));

        Player player = new Player("SACRI");

        InGamePlayer playerGame = new InGamePlayer(player);

        // System.out.print("value: " + c1.awardPoints(playerGame));

        assertEquals(4, c1.awardPoints(playerGame));


    }


}

// Test
// - awardPoints() -> PASSED