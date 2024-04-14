package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InGamePlayerTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){});
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){});
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){});

    @Test
    void placeCard(){

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(1));

        assertDoesNotThrow(() -> playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT));
    }

    @Test
    void notACornerCheck() throws Exception{

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(1));
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(1));
        playerGame.addCardToHand(goldCards.getFirst());

        playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);
        playerGame.placeCard(new GenericPair<>(0, 2), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertThrows(InvalidCardPositionException.class, () -> playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));
    }

    @Test
    void addCardToHand() {

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
        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(c0);

        boolean result = playerGame.getCardsInHand().contains(c0);

        assertTrue(result);
    }

    @Test
    void incrementOwnedResource_1() throws Throwable{

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(1));
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK);

        assertEquals(2, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.WOLF));

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(1));

        playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(4, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.WOLF));

        playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(6, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.WOLF));
    }

    @Test
    void incrementOwnedResource_2() throws Throwable{  // OK
        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.getFirst());
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(1, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.WOLF));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.GRASS));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.BUTTERFLY));

        playerGame.addCardToHand(resourceCards.get(30));
        playerGame.addCardToHand(goldCards.get(33));

        playerGame.placeCard(new GenericPair<>(1, -1), playerGame.getCardsInHand().getFirst(), Side.BACK);

        assertEquals(1, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.WOLF));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.GRASS));
        assertEquals(2, playerGame.getOwnedResources().get(Resource.BUTTERFLY));

        assertThrows(NotEnoughResourcesException.class, () -> playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));
        //FIXME: non so se questo fosse l'intento del test, vedete voi
    }

    @Test
    void setSecretObjective() {  // OK
        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, 1, Resource.WOLF);

        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();

        condition.add(T1);
        condition.add(T2);
        condition.add(T3);

        PatternCondition p = new PatternCondition(condition);

        ObjectiveCard c_o = new ObjectiveCard(3, 1, p);

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.setSecretObjective(c_o);

        assertEquals(c_o, playerGame.getSecretObjective());
    }

    @Test
    void ResourceRecalcTest() throws Throwable{
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);
        ResourceCard c2 = resourceCards.get(21);
        Player player = new Player("giovanni");
        InGamePlayer playerGame = new InGamePlayer(player);
        playerGame.addCardToHand(c0);
        playerGame.addCardToHand(c1);
        playerGame.addCardToHand(c2);
        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT);
        playerGame.placeCard(new GenericPair<>(1, 1), c2, Side.FRONT);

        //fallirà perché era fatto su carte inesistenti ma ora abbiamo prese le carte dal deck
        assertEquals(10, playerGame.getOwnedResources().get(Resource.WOLF));
    }

    @Test
    void CardNotInHandNotPlaced() throws Throwable{
        InitialCard c0 = initialCards.getFirst();
        ResourceCard c1 = resourceCards.get(20);

        Player player = new Player("giovanni");
        InGamePlayer playerGame = new InGamePlayer(player);
        playerGame.addCardToHand(c0);

        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertThrows(CardNotInHandException.class, () -> playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));
        //Vedi sopra
    }

    @Test
    void GoldNeededResourcesNotSat() throws Throwable{
        // TODO :Test run Correctly But should not for NumberOFTimesSatisfied , check in future
        InitialCard c0 = initialCards.getFirst();
        GoldCard c1 = goldCards.getFirst();

        Player player = new Player("giovanni");
        InGamePlayer playerGame = new InGamePlayer(player);
        playerGame.addCardToHand(c0);
        playerGame.addCardToHand(c1);
        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        //fallirà perché le carte erano non esistenti e ne abbiamo messe di esistenti a caso
        assertThrows(NotEnoughResourcesException.class, () -> playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));
    }

    @Test
    void PointsAwarded() {
        checkCorrectPointsAddedTo "Player.getPoints";
    }
}