package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class FieldTest {

    @Test
    void addCard() {

        HashMap<GenericPair<Integer, Integer>, PlayableCard> cards = new HashMap<GenericPair<Integer, Integer>, PlayableCard>();

        ArrayList<GenericPair<Integer, Integer>> corner = new ArrayList<GenericPair<Integer, Integer>>();

        GenericPair x = new GenericPair(0, 0);
        GenericPair y = new GenericPair(1, 1);
        GenericPair z = new GenericPair(-1, -1);

        corner.add(x);
        corner.add(y);
        corner.add(z);

        Image front = new Image();
        Image back = new Image();
        Resource[][] corners = new Resource[2][4];
        ArrayList<Resource> centerBackResources = new ArrayList<Resource>();

        PlayableCard card = new PlayableCard(10, 0, front, back, corners, centerBackResources);

        boolean value = Field.addCard(corner, card);

    }
}