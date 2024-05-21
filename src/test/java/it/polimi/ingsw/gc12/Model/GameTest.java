package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.GameStates.PlayerTurnPlayState;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Player player1;
    Player player2;
    GameLobby lobby;
    Game game;

    @BeforeEach
    void setGameParameters() {
        player1 = new Player("Sacri");
        player2 = new Player("Piants");
        lobby = new GameLobby(player1, 2);
        game = new Game(lobby);
    }

    @Test
    void nextPlayer() {
        lobby.addPlayer(player2);
        game.getCurrentState().nextPlayer();  // don't touch this line
        assertEquals(game.getPlayers().get(0), game.getCurrentPlayer());
    }

    @Test
    void getCurrentPlayer() {
        assertInstanceOf(InGamePlayer.class, game.getCurrentPlayer());
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

}