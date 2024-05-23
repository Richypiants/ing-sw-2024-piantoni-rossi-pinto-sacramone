package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.PlayerTurnPlayState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.SetupState;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Player player1;
    Player player2;
    GameLobby lobby;
    Game game;

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("testPlayer_1");
        player2 = new Player("testPlayer_2");
        lobby = new GameLobby(player1, 2);
        lobby.addPlayer(player2);
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
        assertEquals(0, game.getTurnNumber());
        assertNotNull(game.getResourceCardsDeck());
        assertNotNull(game.getGoldCardsDeck());
        assertEquals(2, game.getCommonObjectives().length);
        assertInstanceOf(SetupState.class, game.getCurrentState());
    }

    @Test
    void everyPlayerReturnsToLobby(){
        GameLobby lobby = game.toLobby();
        assertEquals(game.getPlayers().size(), lobby.getPlayers().size());
    }

    @Test
    void toLobbyRemovingAnInactivePlayers(){
        game.getPlayers().getFirst().toggleActive();
        GameLobby lobby = game.toLobby();

        assertEquals(game.getPlayers().size()-1, game.getActivePlayers().size());
        assertEquals(game.getActivePlayers().size(), lobby.getPlayers().size());
    }

    @Test
    void getPlayers(){
        ArrayList<InGamePlayer> playersOfThisGame = game.getPlayers();
        assertEquals( game.getPlayers().size(), playersOfThisGame.size());
        assertEquals(game.getMaxPlayers(), playersOfThisGame.size());
    }

    @Test
    void getActivePlayers(){
        ArrayList<InGamePlayer> activePlayersOfThisGame = game.getActivePlayers();
        assertTrue(activePlayersOfThisGame.size() <= game.getMaxPlayers() && activePlayersOfThisGame.size() <= game.getPlayers().size());
    }

    @Test
    void nextPlayer() {
        lobby.addPlayer(player2);
        game.getCurrentState().nextPlayer();  // don't touch this line
        assertEquals(game.getPlayers().getFirst(), game.getCurrentPlayer());
    }

    @Test
    void getCurrentPlayer() {
        var result = game.getCurrentPlayer();
        if(result != null)
            assertInstanceOf(InGamePlayer.class, result);
    }

    @Test
    void drawFromCorrect() throws EmptyDeckException {
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getResourceCardsDeck()));
    }

    @Test
    void drawFromVisibleCardsResource() throws EmptyDeckException {
        assertInstanceOf(ResourceCard.class, game.drawFrom(game.getPlacedResources(), 0));
    }

    @Test
    void drawFromVisibleCardsGold() throws EmptyDeckException {
        assertInstanceOf(GoldCard.class, game.drawFrom(game.getPlacedGolds(), 0));
    }

    @Test
    void emptyDeck() throws Throwable {
        for (int i = 0; i < 38; i++) {
            game.drawFrom(game.getResourceCardsDeck());
        }
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getResourceCardsDeck()));

        for (int i = 0; i < 38; i++) {
            game.drawFrom(game.getGoldCardsDeck());
        }
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getGoldCardsDeck()));
    }

    @Test
    void emptyVisibleCardArrays() throws Throwable {
        for (int i = 0; i < 39; i++) {
            game.drawFrom(game.getPlacedGolds(), 0);
        }
        game.drawFrom(game.getPlacedGolds(), 1);
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedGolds(), 0));
        assertThrows(EmptyDeckException.class, () -> game.drawFrom(game.getPlacedGolds(), 1));
    }

    @Test
    void peekFromTest() throws Throwable {
        assertInstanceOf(PlayableCard.class, game.peekFrom(game.getResourceCardsDeck()));
    }

    @Test
    void setStateTest() throws Throwable {
        game.setState(new PlayerTurnPlayState(game, 1, 0));
        assertInstanceOf(PlayerTurnPlayState.class, game.getCurrentState());
    }

    @Test
    void generateDTOTest() throws Throwable {

        assertInstanceOf(ClientGame.class, game.generateDTO(game.getPlayers().getFirst()));
    }

    @Test
    void getActivePlayerTest() throws Throwable {

        assertEquals(game.getPlayers(), game.getActivePlayers());
    }

    @Test
    void getTurnNumberTest() throws Throwable {

        assertEquals(0, game.getTurnNumber());
    }

    @Test
    void generateTemporaryFieldTest() throws Throwable {
        Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> Test = game.generateTemporaryFieldsToPlayers();
        assertInstanceOf(Map.class, Test);
        assert (!Test.isEmpty());

    }


}