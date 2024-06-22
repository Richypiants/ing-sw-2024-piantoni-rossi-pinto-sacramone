package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientPlayerTest {
    static Player originalPlayer;
    static ClientPlayer testedPlayer;
    static EnumMap<Resource, Integer> originalResources;
    static int initialPoints = 0;
    static ViewModel viewModel = new ViewModel();

    static void originalResourcesSetup(int value){
        originalResources = new EnumMap<>(Resource.class);
        for(Resource r : Resource.values()) {
            originalResources.put(r, value);
        }
    }

    @BeforeAll
    static void playerSetup(){
        originalPlayer = new Player("testedPlayer");
        originalResourcesSetup(0);

        testedPlayer = new ClientPlayer(originalPlayer, new ArrayList<>(), originalResources, initialPoints);
    }

    @Test
    void operationsOnOwnedResources(){
        assertEquals(originalResources, testedPlayer.getOwnedResources());

        originalResourcesSetup(1);

        testedPlayer.setOwnedResources(originalResources);
        assertEquals(originalResources, testedPlayer.getOwnedResources());
    }

    @Test
    void successfulPlacedCard(){
        ClientCard placedCard = ViewModel.CARDS_LIST.get(1);
        GenericPair<Integer, Integer> coordinatePosition = new GenericPair<>(0, 0);
        Side placedSide = Side.FRONT;

        testedPlayer.placeCard(coordinatePosition, placedCard, placedSide);

        assertTrue(testedPlayer.getPlacedCards().containsKey(coordinatePosition));
        assertEquals(placedCard, testedPlayer.getPlacedCards().get(coordinatePosition).getX());
        assertEquals(placedSide, testedPlayer.getPlacedCards().get(coordinatePosition).getY());
    }

    @Test
    void operationsOnOpenCorners(){
        List<GenericPair<Integer, Integer>> openCorners = new ArrayList<>();
        openCorners.add(new GenericPair<>(1,1));
        openCorners.add(new GenericPair<>(1,-1));
        openCorners.add(new GenericPair<>(-1,1));
        openCorners.add(new GenericPair<>(-1,-1));

        testedPlayer.setOpenCorners(openCorners);
        assertEquals(openCorners, testedPlayer.getOpenCorners());
    }

    @Test
    void operationsOnPoints(){
        int expectedPoints = 10;
        testedPlayer.setPoints(expectedPoints);
        assertEquals(expectedPoints, testedPlayer.getPoints());
    }

    @Test
    void operationsOnActivityStatus(){
        boolean status = testedPlayer.isActive();
        assertTrue(status);

        testedPlayer.toggleActive();
        assertFalse(testedPlayer.isActive());
        testedPlayer.toggleActive();
        assertTrue(testedPlayer.isActive());
    }

}