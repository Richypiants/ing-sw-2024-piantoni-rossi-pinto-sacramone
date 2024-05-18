package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InGamePlayerTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;

    Player player;
    InGamePlayer playerGame;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        });
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });
    }

    @BeforeEach
    void setGameParameters() {

        player = new Player("Sacri");
        playerGame = new InGamePlayer(player);
    }


    @Test
    void placeCard(){
        playerGame.addCardToHand(initialCards.get(1));
        assertDoesNotThrow(() -> playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT));
    }

    @Test
    void notACornerCheck() throws Exception{

        playerGame.addCardToHand(initialCards.get(1));
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(1));
        playerGame.addCardToHand(resourceCards.get(2));

        playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);
        playerGame.placeCard(new GenericPair<>(0, 2), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertThrows(InvalidCardPositionException.class, () -> playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));
    }

    @Test
    void addCardToHand() {
        playerGame.addCardToHand(initialCards.get(0));
        boolean result = playerGame.getCardsInHand().contains(initialCards.get(0));

        assertTrue(result);
    }

    @Test
    void incrementOwnedResource_1() throws Throwable{

        playerGame.addCardToHand(initialCards.get(1));
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK);

        assertEquals(2, playerGame.getOwnedResources().get(Resource.FUNGI));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.ANIMAL));

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(1));

        playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(4, playerGame.getOwnedResources().get(Resource.FUNGI));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.ANIMAL));

        playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(6, playerGame.getOwnedResources().get(Resource.FUNGI));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.ANIMAL));
    }

    @Test
    void incrementOwnedResource_2() throws Throwable {

        playerGame.addCardToHand(initialCards.getFirst());
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(1, playerGame.getOwnedResources().get(Resource.FUNGI));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.ANIMAL));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.PLANT));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.INSECT));

        playerGame.addCardToHand(resourceCards.get(30));
        playerGame.addCardToHand(goldCards.get(33));

        playerGame.placeCard(new GenericPair<>(1, -1), playerGame.getCardsInHand().getFirst(), Side.BACK);

        assertEquals(1, playerGame.getOwnedResources().get(Resource.FUNGI));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.ANIMAL));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.PLANT));
        assertEquals(2, playerGame.getOwnedResources().get(Resource.INSECT));

        assertThrows(NotEnoughResourcesException.class, () -> playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));
    }

    @Test
    void setSecretObjective() {
        playerGame.setSecretObjective(objectiveCards.get(0));
        assertEquals(objectiveCards.get(0), playerGame.getSecretObjective());
    }

    @Test
    void ResourceRecalculationTest() throws Throwable {

        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);
        ResourceCard c2 = resourceCards.get(21);
        ResourceCard c3 = resourceCards.get(22);

        playerGame.addCardToHand(c0);
        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);

        playerGame.addCardToHand(c1);
        playerGame.addCardToHand(c2);
        playerGame.addCardToHand(c3);

        playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT);
        playerGame.placeCard(new GenericPair<>(1, 1), c2, Side.FRONT);
        playerGame.placeCard(new GenericPair<>(2, 2), c3, Side.FRONT);

        assertEquals(6, playerGame.getOwnedResources().get(Resource.ANIMAL));
    }

    @Test
    void CardNotInHandNotPlaced() throws Throwable {

        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);

        playerGame.addCardToHand(c0);

        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertThrows(CardNotInHandException.class, () -> playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));
        //Vedi sopra
    }

    @Test
    void GoldNeededResourcesNotSat() throws Throwable{

        InitialCard c0 = initialCards.getFirst();
        GoldCard c1 = goldCards.getFirst();

        playerGame.addCardToHand(c0);
        playerGame.addCardToHand(c1);
        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);

        assertThrows(NotEnoughResourcesException.class, () -> playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));
    }

    @Test
    void PointsAwarded() throws Throwable {

        playerGame.addCardToHand(initialCards.get(0));
        playerGame.placeCard(new GenericPair<>(0, 0), initialCards.get(0), Side.BACK);

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(2));
        playerGame.placeCard(new GenericPair<>(1, -1), resourceCards.get(0), Side.FRONT);
        playerGame.placeCard(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT);

        playerGame.addCardToHand(resourceCards.get(10));
        playerGame.addCardToHand(resourceCards.get(21));
        playerGame.placeCard(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT);
        playerGame.placeCard(new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT);

        playerGame.addCardToHand(goldCards.get(7));
        playerGame.placeCard(new GenericPair<>(3, 1), goldCards.get(7), Side.FRONT);

        assertEquals(3, playerGame.getPoints());

        playerGame.addCardToHand(goldCards.get(6));
        playerGame.placeCard(new GenericPair<>(-1, 3), goldCards.get(6), Side.FRONT);

        assertEquals(6, playerGame.getPoints());
    }

    @Test
    void toggleActiveTest() {
        player = new Player("Sacri");
        playerGame = new InGamePlayer(player);
        playerGame.toggleActive();
        assert (!playerGame.isActive());
    }
}