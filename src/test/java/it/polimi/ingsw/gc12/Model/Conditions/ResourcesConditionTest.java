package it.polimi.ingsw.gc12.Model.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourcesConditionTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;

    Player player1;
    Lobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
        objectiveCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});
    }

    static Stream<Arguments> provideMultiConditionParameters() {
        return Stream.of(
                Arguments.of(
                        objectiveCards.get(8).getPointsCondition(),
                        1,
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(3), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(5), Side.FRONT)
                        }
                ), Arguments.of(
                        objectiveCards.get(9).getPointsCondition(),
                        2,
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(10), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(12), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, 3), resourceCards.get(11), Side.FRONT)
                        }
                ), Arguments.of(
                        objectiveCards.get(10).getPointsCondition(),
                        1,
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(20), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(21), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(24), Side.FRONT)
                        }
                ), Arguments.of(
                        objectiveCards.get(11).getPointsCondition(),
                        1,
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(30), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(32), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, 3), resourceCards.get(33), Side.FRONT)
                        }
                )
        );
    }

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("giovanni");
        lobby = new Lobby(UUID.randomUUID(), player1, 1);
        game = new Game(lobby);
    }

    @SafeVarargs
    @ParameterizedTest
    @MethodSource("provideMultiConditionParameters")
    final void genericResourcesCheckTest(ResourcesCondition condition, int numberOfTimesSatisfied,
                                         Triplet<GenericPair<Integer, Integer>, PlayableCard, Side>... cardsToPlay)
            throws Exception {

        ObjectiveCard c_o = objectiveCards.getLast();
        InGamePlayer player1InGame = game.getPlayers().getFirst();
        for (var card : cardsToPlay) {
            player1InGame.addCardToHand(card.getY());
            game.placeCard(player1InGame, card.getX(), card.getY(), card.getZ());
        }

        assertEquals(numberOfTimesSatisfied, condition.numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }

    @Test
    void getConditionParametersTest(){
        Map<Resource, Integer> resourcesToOwn = new EnumMap<>(Resource.class);
        resourcesToOwn.put(Resource.SCROLL, 1);
        resourcesToOwn.put(Resource.QUILL, 1);
        resourcesToOwn.put(Resource.INK, 1);

        ResourcesCondition resourceCondition = new ResourcesCondition(resourcesToOwn);
        assertEquals(resourcesToOwn, resourceCondition.getConditionParameters());
    }
}