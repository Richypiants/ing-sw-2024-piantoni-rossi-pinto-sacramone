package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
    @Test
    void addInitialCardTest(){  // OK

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
        InitialCard c0 = new InitialCard(0, 2 , corner, back);
        Player p1 = new Player("giovanni");
        InGamePlayer p1_g = new InGamePlayer(p1);
        p1_g.addCardToHand(c0);
        assertDoesNotThrow(() -> p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT));
    }

    @Test
    void cardUndefinedNotAdded() throws Throwable{  // addCard()

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
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);

        InitialCard c0 = new InitialCard(0, 2, corner, back);
        p1_g.addCardToHand(c0);
        p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertDoesNotThrow(() -> p1_g.placeCard(new GenericPair<>(1, -1), null, Side.FRONT));

        // ERRORE:
        // Il test da come risultato "false" ma dovrebbe restituire una nullPointerException dato che
        // addCard cerca di istanziare un GenericPair con all'interno un valore "null".

    }

    @Test
    void getCardCoordinates() throws Throwable{
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
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);

        InitialCard c0 = new InitialCard(0, 2, corner, back);
        p1_g.addCardToHand(c0);
        p1_g.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        GenericPair<Integer, Integer> coordinate_res = new GenericPair<>(p1_g.getCardCoordinates(c0).getX(), p1_g.getCardCoordinates(c0).getX());
        assertEquals(new GenericPair<>(0, 0), coordinate_res);



    }
}