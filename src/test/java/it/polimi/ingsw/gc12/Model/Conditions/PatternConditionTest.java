package it.polimi.ingsw.gc12.Model.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static it.polimi.ingsw.gc12.Model.Cards.CardDeckTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO: think about all possible fails and tests

class PatternConditionTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;

    Player player1;
    Lobby lobby;
    Game game;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = loadCardDeckAsArrayList(RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        goldCards = loadCardDeckAsArrayList(GOLD_DECK_FILENAME, new TypeToken<>(){});
        initialCards = loadCardDeckAsArrayList(INITIAL_DECK_FILENAME, new TypeToken<>(){});
        objectiveCards = loadCardDeckAsArrayList(OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});
    }

    static Stream<Arguments> provideMultiConditionParameters(){
        return Stream.of(
                /*
                 * Reversed L Blue Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{
                                new PatternCondition(
                                        List.of(
                                                new Triplet<>(0, 0, Resource.ANIMAL),
                                                new Triplet<>(0, -2, Resource.ANIMAL),
                                                new Triplet<>(1, 1, Resource.ANIMAL)
                                        )
                                )
                        },
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.BACK),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(20), Side.BACK),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(21), Side.BACK),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(22), Side.BACK)
                        }
                ),
                /*
                 * Diagonal Red Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.getFirst().getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(3), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(5), Side.FRONT)
                        }
                ),
                /*
                 * Diagonal Green Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(1).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(10), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(11), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, 3), resourceCards.get(12), Side.FRONT)
                        }
                ),
                /*
                 * Diagonal Blue Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(2).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(20), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(21), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(24), Side.FRONT)
                        }
                ),
                /*
                 * Diagonal Purple Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(3).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(30), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(32), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, 3), resourceCards.get(33), Side.FRONT)
                        }
                ),
                /*
                 *  L Red - Green Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(4).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(2), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, -2), resourceCards.get(12), Side.FRONT)
                        }
                ),
                /*
                 *  L Green - Purple Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(5).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(10), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(12), Side.FRONT),
                                new Triplet<>(new GenericPair<>(0, -2), resourceCards.get(30), Side.FRONT)
                        }
                ),
                /*
                 *  L Blue - Red Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(6).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(20), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(22), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(3), Side.FRONT)
                        }
                ),
                /*
                 *  L Purple - Blue Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(7).getPointsCondition()},
                        new int[]{1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(30), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(32), Side.FRONT),
                                new Triplet<>(new GenericPair<>(0, 2), resourceCards.get(22), Side.FRONT)
                        }
                ),
                /*
                 * Two Red diagonals in a row
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.getFirst().getPointsCondition()},
                        new int[]{2},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(4), Side.BACK),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(9), Side.BACK),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(8), Side.BACK),
                                new Triplet<>(new GenericPair<>(4, 4), resourceCards.getFirst(), Side.BACK),
                                new Triplet<>(new GenericPair<>(5, 5), resourceCards.get(3), Side.BACK),
                                new Triplet<>(new GenericPair<>(6, 6), resourceCards.get(6), Side.BACK)
                        }
                ),
                /*
                 * L Purple - Blue recognised twice
                 */
                Arguments.of(
                        new PatternCondition[]{(PatternCondition) objectiveCards.get(7).getPointsCondition()},
                        new int[]{2},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(30), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(32), Side.FRONT),
                                new Triplet<>(new GenericPair<>(0, 2), resourceCards.get(22), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(31), Side.BACK),
                                new Triplet<>(new GenericPair<>(-1, -1), resourceCards.get(33), Side.BACK),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(24), Side.BACK)
                        }
                ),
                /*
                 * Multipattern Mix, scrivere cosa sia
                 */
                Arguments.of(
                        new PatternCondition[]{
                                (PatternCondition) objectiveCards.get(7).getPointsCondition(),
                                (PatternCondition) objectiveCards.getFirst().getPointsCondition()
                        },
                        new int[]{1, 1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(30), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, -1), resourceCards.get(32), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(22), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(3), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(5), Side.FRONT)
                        }
                ),
                /*
                 * Intersecting Pattern
                 */
                Arguments.of(
                        new PatternCondition[]{
                                (PatternCondition) objectiveCards.get(4).getPointsCondition(),
                                (PatternCondition) objectiveCards.getFirst().getPointsCondition()
                        },
                        new int[]{1, 1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(2), Side.BACK),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(5), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(3), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 3), resourceCards.get(6), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 0), resourceCards.get(10), Side.FRONT)
                        }
                ),
                /*
                 * Game simulation
                 */
                Arguments.of(
                        new PatternCondition[]{
                                (PatternCondition) objectiveCards.get(1).getPointsCondition(),
                                (PatternCondition) objectiveCards.get(0).getPointsCondition(),
                                (PatternCondition) objectiveCards.get(6).getPointsCondition(),
                                (PatternCondition) objectiveCards.get(7).getPointsCondition(),
                                (PatternCondition) objectiveCards.get(4).getPointsCondition(),
                                (PatternCondition) objectiveCards.get(5).getPointsCondition()
                        },
                        new int[]{1, 1, 2, 2, 1, 1},
                        new Triplet[]{
                                new Triplet<>(new GenericPair<>(0, 0), initialCards.getFirst(), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, 1), resourceCards.get(11), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, -1), resourceCards.get(13), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, 1), resourceCards.get(20), Side.FRONT),
                                new Triplet<>(new GenericPair<>(1, -1), resourceCards.get(21), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, 2), resourceCards.get(0), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, 3), resourceCards.get(3), Side.FRONT),
                                new Triplet<>(new GenericPair<>(4, 4), resourceCards.get(4), Side.FRONT),
                                new Triplet<>(new GenericPair<>(5, 5), resourceCards.get(6), Side.FRONT),
                                new Triplet<>(new GenericPair<>(4, 2), resourceCards.get(5), Side.FRONT),
                                new Triplet<>(new GenericPair<>(5, 1), resourceCards.get(10), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, -2), resourceCards.get(32), Side.FRONT),
                                new Triplet<>(new GenericPair<>(3, -3), resourceCards.get(30), Side.FRONT),
                                new Triplet<>(new GenericPair<>(2, -4), resourceCards.get(33), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, 2), resourceCards.get(8), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, 3), resourceCards.get(16), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-4, 4), resourceCards.get(18), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-5, 5), resourceCards.get(12), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, 1), resourceCards.get(23), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, -2), resourceCards.get(35), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-1, -3), resourceCards.get(28), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-2, -4), resourceCards.get(39), Side.FRONT),
                                new Triplet<>(new GenericPair<>(-3, -1), resourceCards.get(26), Side.FRONT),
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
    final void genericMultiPatternTest(PatternCondition[] conditions, int[] numberOfTimesSatisfied,
                                        Triplet<GenericPair<Integer, Integer>, PlayableCard, Side>... cardsToPlay)
            throws Exception {
        ObjectiveCard c_o = objectiveCards.getFirst();

        InGamePlayer player1InGame = game.getPlayers().getFirst();
        for (var card : cardsToPlay) {
            player1InGame.addCardToHand(card.getY());
            game.placeCard(player1InGame, card.getX(), card.getY(), card.getZ());
        }

        for(int i = 0 ; i < conditions.length; i++)
            assertEquals(numberOfTimesSatisfied[i], conditions[i].numberOfTimesSatisfied(c_o, game.getPlayers().getFirst()));
    }
}