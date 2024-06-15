package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class InitialCardTest {
    private static final ArrayList<InitialCard> initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});;

    @Test
    void initialCardConstructorTest(){
        int id = 80;
        int pointsGranted = 0;
        Map<GenericPair<Integer, Integer>, Resource> frontCorners = new HashMap<>();
        frontCorners.put(new GenericPair<>(1,1), Resource.ANIMAL);
        frontCorners.put(new GenericPair<>(-1,1), Resource.NOT_A_CORNER);
        frontCorners.put(new GenericPair<>(1, -1), Resource.FUNGI);
        frontCorners.put(new GenericPair<>(-1, -1), Resource.INSECT);

        Map<GenericPair<Integer, Integer>, Resource> backCorners = new HashMap<>();
        backCorners.put(new GenericPair<>(1,1), Resource.EMPTY);
        backCorners.put(new GenericPair<>(-1,1), Resource.EMPTY);
        backCorners.put(new GenericPair<>(1, -1), Resource.EMPTY);
        backCorners.put(new GenericPair<>(-1, -1), Resource.EMPTY);

        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corners = new HashMap<>();
        corners.put(Side.FRONT, frontCorners);
        corners.put(Side.BACK, backCorners);

        Map<Resource, Integer> placeHolderResources = new EnumMap<>(Resource.class);
        placeHolderResources.put(Resource.FUNGI, 1);
        placeHolderResources.put(Resource.INSECT, 1);
        placeHolderResources.put(Resource.ANIMAL, 1);
        placeHolderResources.put(Resource.PLANT, 0);

        InitialCard thisInitialCard = new InitialCard(
                id,
                pointsGranted,
                corners,
                placeHolderResources
        );

        assertEquals(id, thisInitialCard.ID);
        assertEquals(pointsGranted, thisInitialCard.POINTS_GRANTED);
        assertEquals(corners.get(Side.FRONT), thisInitialCard.getCorners(Side.FRONT));
        assertEquals(corners.get(Side.BACK), thisInitialCard.getCorners(Side.BACK));
        assertEquals(placeHolderResources, thisInitialCard.getCenterBackResources());
    }

    @Test
    void toStringTest(){
        assertInstanceOf(String.class, initialCards.getFirst().toString());
    }
}
