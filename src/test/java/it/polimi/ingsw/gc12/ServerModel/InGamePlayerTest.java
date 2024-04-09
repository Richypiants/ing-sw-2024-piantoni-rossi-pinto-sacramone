package it.polimi.ingsw.gc12.ServerModel;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.GoldCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.ServerModel.Conditions.CornersCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InGamePlayerTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){});
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){});
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){});
    ArrayList<ObjectiveCard> objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){});

    @Test
    void placeCard() {

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(1));

        assertEquals(true, playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT));
    }

    @Test
    void notACornerCheck() {

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(1));

        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(1));
        playerGame.addCardToHand(goldCards.get(0));

        playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);
        playerGame.placeCard(new GenericPair<>(0, 2), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(false, playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));
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

        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(c0);

        boolean result = playerGame.getCardsInHand().contains(c0);

        assertEquals(true, result);


    }

    @Test
    void incrementOwnedResource_1() {

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(1));
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK);

        assertEquals(2, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(1, playerGame.getOwnedResources().get(Resource.WOLF));

        playerGame.addCardToHand(resourceCards.get(0));
        playerGame.addCardToHand(resourceCards.get(1));
        playerGame.addCardToHand(goldCards.get(0));

        playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(4, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.WOLF));

        playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(6, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.WOLF));
    }

    @Test
    void incrementOwnedResource_2() {  // OK
        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.addCardToHand(initialCards.get(0));
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

        assertEquals(false, playerGame.placeCard(new GenericPair<>(1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT));

        /*
        assertEquals(1, playerGame.getOwnedResources().get(Resource.MUSHROOM));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.WOLF));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.GRASS));
        assertEquals(2, playerGame.getOwnedResources().get(Resource.BUTTERFLY));
        assertEquals(0, playerGame.getOwnedResources().get(Resource.FEATHER));
         */
    }

    @Test
    void setSecretObjective() {  // OK


        Triplet<Integer, Integer, Resource> T1 = new Triplet<Integer, Integer, Resource>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<Integer, Integer, Resource>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<Integer, Integer, Resource>(1, 1, Resource.WOLF);

        ArrayList<Triplet<Integer, Integer, Resource>> Array = new ArrayList<Triplet<Integer, Integer, Resource>>();

        Array.add(T1);
        Array.add(T2);
        Array.add(T3);

        PatternCondition p = new PatternCondition(Array);

        ObjectiveCard c_o = new ObjectiveCard(3, 1, null, null, p);

        Player player = new Player("SACRI");
        InGamePlayer playerGame = new InGamePlayer(player);

        playerGame.setSecretObjective(c_o);

        assertEquals(c_o, playerGame.getSecretObjective());

    }

    @Test
    void ResourceRecalcTest() {
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, back);
        ResourceCard c2 = new ResourceCard(2, 1, null, null, corner, back);
        Player player = new Player("giovanni");
        InGamePlayer playerGame = new InGamePlayer(player);
        playerGame.addCardToHand(c0);
        playerGame.addCardToHand(c1);
        playerGame.addCardToHand(c2);
        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT);
        playerGame.placeCard(new GenericPair<>(1, 1), c2, Side.FRONT);


        assertEquals(10, playerGame.getOwnedResources().get(Resource.WOLF));

    }

    @Test
    void CardNotInHandNotPlaced() {
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        ResourceCard c1 = new ResourceCard(1, 2, null, null, corner, back);

        Player player = new Player("giovanni");
        InGamePlayer playerGame = new InGamePlayer(player);
        playerGame.addCardToHand(c0);

        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertFalse(playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));
    }

    @Test
    void GoldNeededResourcesNotSat() {
        // TODO :Test run Correctly But should not for NumberOFTimesSatisfied , check in future
        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(-1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(1, -1), Resource.WOLF);
        resource.put(new GenericPair<>(-1, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);
        Map<Resource, Integer> back = new EnumMap<>(Resource.class);
        back.put(Resource.WOLF, 1);
        Map<Resource, Integer> Needed = new HashMap<>();
        Needed.put(Resource.WOLF, 10);
        PointsCondition p = new CornersCondition();
        ResourcesCondition RP = new ResourcesCondition(Needed);
        InitialCard c0 = new InitialCard(0, 2, null, null, corner, back);
        GoldCard c1 = new GoldCard(1, 2, null, null, corner, back, p, RP);

        Player player = new Player("giovanni");
        InGamePlayer playerGame = new InGamePlayer(player);
        playerGame.addCardToHand(c0);
        playerGame.addCardToHand(c1);
        playerGame.placeCard(new GenericPair<>(0, 0), c0, Side.FRONT);
        assertFalse(playerGame.placeCard(new GenericPair<>(1, -1), c1, Side.FRONT));

    }
}