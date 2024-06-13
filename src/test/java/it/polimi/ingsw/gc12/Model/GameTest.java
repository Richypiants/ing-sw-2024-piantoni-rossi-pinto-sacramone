package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Player player1;
    Player player2;
    Lobby lobby;
    Game game;

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("testPlayer_1");
        player2 = new Player("testPlayer_2");
        lobby = new Lobby(UUID.randomUUID(), player1, 2);
        try {
            lobby.addPlayer(player2);
        } catch (FullLobbyException ignored) {
        }
        game = new Game(lobby);
    }

    /**
     * When a Game is created, some preliminary operations should be executed in a well-constructed game:
     *  1. Players are randomly shuffled from the Lobby, which is maintaining join-order.
     *  2. The round is set to 0
     *  3. The resourceCardDeck and goldCardDeck are created and the associated common cards are set.
     *  4. The gameState is a SetupState.
    * */

    @Test
    void gameConstructorTest(){
        assertEquals( lobby.getPlayers().size(), game.getPlayers().size());
        assertEquals(0, game.getRoundNumber());
        assertNotNull(game.getResourceCardsDeck());
        assertNotNull(game.getGoldCardsDeck());
        assertEquals(2, game.getCommonObjectives().length);
    }

    @Test
    void everyPlayerReturnsToLobby(){
        Lobby lobby = game.toLobby();
        assertEquals(game.getPlayers().size(), lobby.getPlayers().size());
    }

    @Test
    void toLobbyRemovingAnInactivePlayers(){
        game.getPlayers().getFirst().toggleActive();
        Lobby lobby = game.toLobby();

        assertEquals(game.getPlayers().size()-1, game.getActivePlayers().size());
        assertEquals(game.getActivePlayers().size(), lobby.getPlayers().size());
    }

    //FIXME: this test has to be corrected after moving attributes from states to game
    /*@Test
    void getPlayers(){
        ArrayList<InGamePlayer> playersOfThisGame = game.getPlayers();
        assertEquals( game.getPlayers().size(), playersOfThisGame.size());
        assertEquals(game.getMaxPlayers(), playersOfThisGame.size());
    }*/

    //FIXME: this test has to be corrected after moving attributes from states to game
    /*@Test
    void getActivePlayers(){
        ArrayList<InGamePlayer> activePlayersOfThisGame = game.getActivePlayers();
        assertTrue(activePlayersOfThisGame.size() <= game.getMaxPlayers() && activePlayersOfThisGame.size() <= game.getPlayers().size());
    }*/

    //FIXME: this test has to be corrected after moving attributes from states to game
    /*@Test
    void nextPlayer() {
        lobby.addPlayer(player2);
        game.getCurrentState().nextPlayer();
        assertEquals(game.getPlayers().getFirst(), game.getCurrentPlayer());
    }*/

    @Test
    void increaseRound() {
        Game tempGame = new Game(lobby);
        int initialTurnNumber = tempGame.getRoundNumber();
        tempGame.increaseRound();
        assertEquals(initialTurnNumber + 1, tempGame.getRoundNumber());
    }

    //FIXME: this test has to be corrected after moving attributes from states to game
    /*@Test
    void getCurrentPlayer() {
        game.setState(new PlayerTurnDrawState(game, 0, -1));
        InGamePlayer actualCurrentPlayer = game.getCurrentPlayer();
        assertNotNull(actualCurrentPlayer);
        assertTrue(game.getPlayers().contains(actualCurrentPlayer));
    }*/

    @Test
    void setCommonObjectives(){
        ArrayList<ObjectiveCard> objectiveCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});
        game.setCommonObjectives(new ObjectiveCard[]{objectiveCards.getFirst(), objectiveCards.getLast()});

        assertEquals(game.getCommonObjectives().length, game.getCommonObjectives().length);
        assertEquals(objectiveCards.getFirst(), game.getCommonObjectives()[0]);
        assertEquals(objectiveCards.getLast(), game.getCommonObjectives()[1]);
    }

    @Test
    void generateCommonObjectives(){
        game.generateCommonObjectives();

        assertEquals(game.getCommonObjectives().length, game.getCommonObjectives().length);
        assertInstanceOf(ObjectiveCard.class, game.getCommonObjectives()[0]);
        assertInstanceOf(ObjectiveCard.class, game.getCommonObjectives()[1]);
    }

    @Test
    void successfulDrawFrom() throws EmptyDeckException {
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getResourceCardsDeck()));
    }

    @Test
    void unsuccessfulDrawFrom() throws EmptyDeckException {
        while(!game.getResourceCardsDeck().isEmpty())
            game.drawFrom(game.getResourceCardsDeck());

        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getResourceCardsDeck()));
    }

    @Test
    void successfulDrawFromVisibleResourceCards(){
        for(int indexToDrawFrom = 0; indexToDrawFrom < game.getPlacedResources().length; indexToDrawFrom++) {
            int finalIndexToDrawFrom = indexToDrawFrom;
            assertDoesNotThrow(() -> {
                PlayableCard drawnCard = game.drawFrom(game.getPlacedResources(), finalIndexToDrawFrom);
                assertInstanceOf(ResourceCard.class, drawnCard);
                assertNotNull(game.getPlacedResources()[finalIndexToDrawFrom]);
            });
        }
    }

    @Test
    void successfulDrawFromVisibleGoldCards(){
        for(int indexToDrawFrom = 0; indexToDrawFrom < game.getPlacedGolds().length; indexToDrawFrom++) {
            int finalIndexToDrawFrom = indexToDrawFrom;
            assertDoesNotThrow(() -> {
                PlayableCard drawnCard = game.drawFrom(game.getPlacedGolds(), finalIndexToDrawFrom);
                assertInstanceOf(GoldCard.class, drawnCard);
                assertNotNull(game.getPlacedGolds()[finalIndexToDrawFrom]);
            });
        }
    }

    @Test
    void emptyDeck(){
        for (int i = 0; i < 38; i++)
            assertDoesNotThrow( () -> game.drawFrom(game.getResourceCardsDeck()));
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getResourceCardsDeck()));

        for (int i = 0; i < 38; i++)
            assertDoesNotThrow( () -> game .drawFrom(game.getGoldCardsDeck()));
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getGoldCardsDeck()));
    }

    @Test
    void drawFromEmptyVisibleGoldCardArrays(){
        for (int i = 0; i < 39; i++)
            assertDoesNotThrow( () -> game.drawFrom(game.getPlacedGolds(), 0));

        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedGolds(), 1));

        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedGolds(), 0));
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedGolds(), 1));
    }

    @Test
    void drawFromEmptyVisibleResourceCardArrays(){
        for (int i = 0; i < 39; i++)
            assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 0));

        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 1));

        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedResources(), 0));
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedResources(), 1));
    }

    @Test
    void successfulPeekFromTest(){
        assertInstanceOf(PlayableCard.class, game.peekFrom(game.getResourceCardsDeck()));
    }

    //FIXME: this test has to be corrected after moving attributes from states to game
    /*@Test
    void setStateTest(){
        game.setState(new PlayerTurnPlayState(game, 1, 0));
        assertInstanceOf(PlayerTurnPlayState.class, game.getCurrentState());
    }*/

    @Test
    void successfulDecreaseFinalPhaseCounter(){
        game.initializeFinalPhaseCounter();
        int currentFinalPhaseCounter = game.getFinalPhaseCounter();
        game.decreaseFinalPhaseCounter();
        assertEquals(currentFinalPhaseCounter-1, game.getFinalPhaseCounter());
    }

    @Test
    void coherentGetterValues(){
        //For the correct initialization of the ObjectiveCards;
        GameController gameController = new GameController(game);
        ChooseInitialCardsState initialState = new ChooseInitialCardsState(gameController, game);
        initialState.transition();

        assertInstanceOf(CardDeck.class, game.getResourceCardsDeck());
        assertInstanceOf(ResourceCard.class, game.getResourceCardsDeck().peek());

        assertInstanceOf(CardDeck.class, game.getGoldCardsDeck());
        assertInstanceOf(GoldCard.class, game.getGoldCardsDeck().peek());

        assertInstanceOf(CardDeck.class, game.getObjectiveCardsDeck());
        assertInstanceOf(ObjectiveCard.class, game.getObjectiveCardsDeck().peek());

        assertEquals(2, game.getPlacedResources().length);
        assertEquals(2, game.getPlacedGolds().length);
        assertEquals(2, game.getCommonObjectives().length);

        assertInstanceOf(ResourceCard.class, game.getPlacedResources()[0]);
        assertInstanceOf(ResourceCard.class, game.getPlacedResources()[1]);
        assertInstanceOf(GoldCard.class, game.getPlacedGolds()[0]);
        assertInstanceOf(GoldCard.class, game.getPlacedGolds()[1]);
        assertInstanceOf(ObjectiveCard.class, game.getCommonObjectives()[0]);
        assertInstanceOf(ObjectiveCard.class, game.getCommonObjectives()[1]);
    }

    @Test
    void generateDTOTest(){
        assertInstanceOf(ClientGame.class, game.generateDTO(game.getPlayers().getFirst()));
    }

    @Test
    void generateTemporaryFieldTest(){
        ArrayList<InitialCard> initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
        ArrayList<ResourceCard> resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});

        Game tempGame = new Game(lobby);
        InGamePlayer targetInGamePlayer = tempGame.getPlayers().getFirst();
        targetInGamePlayer.addCardToHand(initialCards.getFirst());
        targetInGamePlayer.addCardToHand(resourceCards.getFirst());
        assertDoesNotThrow( () -> targetInGamePlayer.placeCard(new GenericPair<>(0,0), initialCards.getFirst(), Side.FRONT));
        assertDoesNotThrow( () -> targetInGamePlayer.placeCard(new GenericPair<>(1,1), resourceCards.getFirst(), Side.BACK));

        InGamePlayer targetSecondInGamePlayer = tempGame.getPlayers().get(1);
        targetSecondInGamePlayer.addCardToHand(initialCards.getLast());
        targetSecondInGamePlayer.addCardToHand(resourceCards.getLast());
        assertDoesNotThrow( () -> targetSecondInGamePlayer.placeCard(new GenericPair<>(0,0), initialCards.getLast(), Side.FRONT));
        assertDoesNotThrow( () -> targetSecondInGamePlayer.placeCard(new GenericPair<>(-1,-1), resourceCards.getLast(), Side.BACK));

        Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> restoreGameMap = tempGame.generateTemporaryFieldsToPlayers();

        for(var playerFieldCopiedEntry : restoreGameMap.entrySet()){
            InGamePlayer thisPlayer = tempGame.getPlayers().stream().filter( (player) -> player.getNickname().equals(playerFieldCopiedEntry.getKey())).toList().getFirst();
            for(var cardPlacedEntry: thisPlayer.getPlacedCards().sequencedEntrySet())
                assertTrue(playerFieldCopiedEntry.getValue().containsKey(cardPlacedEntry.getKey()));
        }

    }

}