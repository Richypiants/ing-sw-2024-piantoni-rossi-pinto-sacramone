package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ObjectiveCardTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player;
    Lobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        goldCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.GOLD_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
        objectiveCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});
    }

    @BeforeEach
    void setGameParameters() {
        player = new Player("testPlayer");
        lobby = new Lobby(UUID.randomUUID(), player, 1);
        game = new Game(lobby);
    }

    @Test
    void awardPoints() throws InvalidCardPositionException, NotEnoughResourcesException, CardNotInHandException {
        int expectedAwardedPoints = 2;
        InGamePlayer playerGame = game.getPlayers().getFirst();

        playerGame.setSecretObjective(objectiveCards.get(15));

        playerGame.addCardToHand(initialCards.getFirst());
        game.placeCard(playerGame, new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK);

        playerGame.addCardToHand(resourceCards.get(4));
        game.placeCard(playerGame, new GenericPair<>(1, -1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        playerGame.addCardToHand(goldCards.get(30));
        game.placeCard(playerGame, new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(expectedAwardedPoints, playerGame.getSecretObjective().awardPoints(playerGame));
    }

    @Test
    void objectiveCardConstructorTest(){
        int id = 100;
        int pointsGranted = 3;

        Map<Resource, Integer> placeHolderResources = new EnumMap<>(Resource.class);
        placeHolderResources.put(Resource.FUNGI, 1);
        placeHolderResources.put(Resource.INSECT, 0);
        placeHolderResources.put(Resource.ANIMAL, 1);
        placeHolderResources.put(Resource.PLANT, 1);

        PointsCondition placeHolderPointsCondition = new ResourcesCondition(placeHolderResources);

       ObjectiveCard thisObjectiveCard = new ObjectiveCard(
                id,
                pointsGranted,
                placeHolderPointsCondition
        );

        assertEquals(id, thisObjectiveCard.ID);
        assertEquals(pointsGranted, thisObjectiveCard.POINTS_GRANTED);
        assertEquals(placeHolderPointsCondition, thisObjectiveCard.getPointsCondition());
    }

    @Test
    void toStringTest() {
        assertInstanceOf(String.class, objectiveCards.getFirst().toString());
    }
}