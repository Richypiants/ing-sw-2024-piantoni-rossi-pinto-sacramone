package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayableCardTest {

    @Test
    void getCornerResource() {

        Resource[][] corner = {{Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF},
                {Resource.WOLF, Resource.WOLF}, {Resource.WOLF, Resource.WOLF}};

        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, new EnumMap<>(Resource.class));

        assertEquals(Resource.SCROLL, c1.getCornerResource(2, Side.FRONT));

        // TODO: WIP (corner è stato cambiato da piants da un array ad una HashMap)
    }

    @Test
    void getCorners() {

    }
}