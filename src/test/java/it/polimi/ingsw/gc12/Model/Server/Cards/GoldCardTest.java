package it.polimi.ingsw.gc12.Model.Server.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Server.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GoldCardTest{

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    Player player;
    Lobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        goldCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.GOLD_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
    }

    @BeforeEach
    void setGameParameters() {
        player = new Player("TestPlayer");
        lobby = new Lobby(UUID.randomUUID(), player, 1);
        game = new Game(lobby);
    }

    @Test
    void goldCardConstructorTest(){
        int id = 10;
        int pointsGranted = 1;
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
        placeHolderResources.put(Resource.INSECT, 0);
        placeHolderResources.put(Resource.ANIMAL, 0);
        placeHolderResources.put(Resource.PLANT, 0);

        PointsCondition placeHolderPointsCondition = new ResourcesCondition(placeHolderResources);

        ResourcesCondition placeHolderResourceCondition = new ResourcesCondition(placeHolderResources);

        GoldCard thisGoldCard = new GoldCard(
                id,
                pointsGranted,
                corners,
                placeHolderResources,
                placeHolderPointsCondition,
                placeHolderResourceCondition
        );

        assertEquals(id, thisGoldCard.ID);
        assertEquals(pointsGranted, thisGoldCard.POINTS_GRANTED);
        assertEquals(corners.get(Side.FRONT), thisGoldCard.getCorners(Side.FRONT));
        assertEquals(corners.get(Side.BACK), thisGoldCard.getCorners(Side.BACK));
        assertEquals(placeHolderResources, thisGoldCard.getCenterBackResources());
        assertEquals(placeHolderPointsCondition, thisGoldCard.getPointsCondition());
        assertEquals(placeHolderResourceCondition, thisGoldCard.getNeededResourcesToPlay());
    }

    @Test
    void awardPoints(){
        int expectedPointsEarned = 2;
        InGamePlayer playerGame = game.getPlayers().getFirst();

        //Placed some starting cards to simulate a game initial configuration
        // and earn the necessary Resources to fulfill the GoldCard Condition for placing it.
        playerGame.addCardToHand(initialCards.getFirst());
        assertDoesNotThrow(()-> game.placeCard(playerGame, new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK));
        playerGame.addCardToHand(resourceCards.get(4));
        assertDoesNotThrow(()-> game.placeCard(playerGame, new GenericPair<>(1, -1), playerGame.getCardsInHand().getFirst(), Side.FRONT));

        //The actual card that will be tested
        playerGame.addCardToHand(goldCards.get(30));
        assertDoesNotThrow(()-> game.placeCard(playerGame, new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));

        assertEquals(expectedPointsEarned, playerGame.getPlacedCards().get(new GenericPair<>(-1, 1)).getX().awardPoints(playerGame));
    }

    @Test
    void toStringTest() {
        assertInstanceOf(String.class, goldCards.getFirst().toString());
    }
}