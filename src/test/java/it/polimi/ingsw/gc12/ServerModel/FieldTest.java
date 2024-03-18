package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class FieldTest {

    //TODO: NOT END
    @Test
    void addCard() {
        ArrayList<GenericPair<Integer, Integer>> coo = new ArrayList<GenericPair<Integer, Integer>>();

        GenericPair x = new GenericPair(0, 0);
        GenericPair y = new GenericPair(1, 1);
        GenericPair z = new GenericPair(-1, -1);

        coo.add(x);
        coo.add(y);
        coo.add(z);

        Resource[][] corners = new Resource[2][4];
        ArrayList<Resource> centerBackResources = new ArrayList<Resource>();

        PlayableCard card = new PlayableCard(10, 0, null, null, corners, centerBackResources);

        boolean value = Field.addCard(coo, card);

    }

    @Test
    void getCardCoordinates() {

    }
}