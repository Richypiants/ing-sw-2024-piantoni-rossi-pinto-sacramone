package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTest {
    @Test
    void addInitialCardTest() {  // OK

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.SCROLL);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        EnumMap<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        Player p1 = new Player("giovanni");
        InGamePlayer p1_g = new InGamePlayer(p1);
        assertTrue(p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT));

    }

    @Test
    void cardUndefined() {  // addCard()


        Player p1 = new Player("SACRI");

        InGamePlayer p1_g = new InGamePlayer(p1);

        GenericPair<Integer, Integer> coo = new GenericPair<>(0, 0);

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        EnumMap<Resource, Integer> centerBackResources = new EnumMap<>(Resource.class);

        PlayableCard card = new ResourceCard(10, 0, null, null, corner, centerBackResources);

        // assertEquals(null, p1_g..addCard(coo, null, Side.BACK));

        // ERRORE:
        // Il test da come risultato "false" ma dovrebbe restituire una nullPointerException dato che
        // addCard cerca di istanziare un GenericPair con all'interno un valore "null".


    }

    @Test
    void getCardCoordinates() {

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        HashMap<Side, HashMap<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        EnumMap<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);

        Player p1 = new Player("SACRI");

        InGamePlayer p1_g = new InGamePlayer(p1);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);

        GenericPair<Integer, Integer> result = p1_g.getOwnField().getCardCoordinates(c0);

        GenericPair<Integer, Integer> expected = new GenericPair<>(0, 0);

        assertEquals(expected, result);
    }
}