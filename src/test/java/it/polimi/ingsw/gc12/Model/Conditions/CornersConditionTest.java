package it.polimi.ingsw.gc12.Model.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CornersConditionTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;

    Player player;
    GameLobby lobby;
    Game game;
    CornersCondition corner;

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
        lobby = new GameLobby(player, 1);
        game = new Game(lobby);
        corner = new CornersCondition();
    }

    static Stream<Arguments> provideMultiConditionParameters() {
        return Stream.of(
                /*
                 * Generic Displacement Case
                 */
                Arguments.of(
                        new int[]{0, 1, 1, 1, 1, 1, 1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.BACK),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(21), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 1), resourceCards.get(7), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 3), resourceCards.get(6), Side.FRONT),
                        }
                ),
                /*
                 * Every Corner of Initial Card is Covered
                 */
                Arguments.of(
                        new int[]{0, 1, 1, 2, 1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.BACK),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(0, 2), resourceCards.get(2), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(1), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, -1), resourceCards.get(3), Side.FRONT)
                        }
                ),
                /*
                 * Diamond Card Placement
                 */
                Arguments.of(
                        new int[]{0, 1, 1, 1, 1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.BACK),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(1), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(2), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, -1), resourceCards.get(3), Side.FRONT)
                        }
                )
        );
    }

    @SafeVarargs
    @ParameterizedTest
    @MethodSource("provideMultiConditionParameters")
    final void genericMultiCornersPatternTest(int[] numberOfTimesSatisfied,
                                       Triplet<GenericPair<Integer, Integer>, PlayableCard, Side>... cardsToPlay)
            throws Exception {
        ObjectiveCard c_o = objectiveCards.getFirst();

        InGamePlayer player1InGame = game.getPlayers().getFirst();

        for (int i = 0; i < numberOfTimesSatisfied.length; i++){
            Triplet<GenericPair<Integer, Integer>, PlayableCard, Side> cardPlacement = cardsToPlay[i];
            player1InGame.addCardToHand(cardPlacement.getY());
            player1InGame.placeCard(cardPlacement.getX(), cardPlacement.getY(), cardPlacement.getZ());

            assertEquals(numberOfTimesSatisfied[i], corner.numberOfTimesSatisfied(cardsToPlay[i].getY(), game.getPlayers().getFirst()));
        }
    }
}