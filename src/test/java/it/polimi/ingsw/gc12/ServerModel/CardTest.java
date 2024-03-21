package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Resource;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    @Test
    void awardPoints() {

        Resource[][] corner = {{Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF},
                {Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF}};

        ResourceCard c1 = new ResourceCard(1, 4, null, null, corner, new EnumMap<>(Resource.class));

        Player player = new Player("SACRI");

        InGamePlayer playerGame = new InGamePlayer(player);

        // System.out.print("value: " + c1.awardPoints(playerGame));

        assertEquals(4, c1.awardPoints(playerGame));
    }
}

// Test
// - awardPoints() -> PASSED