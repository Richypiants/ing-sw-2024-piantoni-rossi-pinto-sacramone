package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.*;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidDeckPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The {@code PlayerTurnDrawStateTest} class tests the behavior of the game during a draw state. It ensures that drawing cards from
 * various decks and handling disconnections are managed correctly.
 */
class PlayerTurnDrawStateTest {
    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    InGamePlayer testTarget;
    GameController gameController;

    /**
     * Sets up the game parameters and initializes the game state before each test, doing all the initializing phases.
     */
    @BeforeEach
    void setGameParameters() throws Exception{
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        UUID lobbyUUID = UUID.randomUUID();

        lobby = new Lobby(lobbyUUID, player1, 2);
        lobby.addPlayer(player2);

        game = new Game(lobby);
        gameController = GameController.MODEL.createGameController(game);

        client1 = createNetworkSessionStub(gameController);
        client2 = createNetworkSessionStub(gameController);

        gameController.CONTROLLED_GAME.getPlayers().getFirst().addListener(client1.getListener());
        gameController.CONTROLLED_GAME.getPlayers().getLast().addListener(client2.getListener());

        gameController.putActivePlayer(client1, player1);
        gameController.putActivePlayer(client2, player1);

        gameController.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(0, 0), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);
        gameController.getCurrentState().placeCard(game.getPlayers().getLast(), new GenericPair<>(0, 0), game.getPlayers().getLast().getCardsInHand().getFirst(), Side.FRONT);

        int choice1 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst();
        int choice2 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client2.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst();

        gameController.getCurrentState().pickObjective(game.getPlayers().getFirst(), (ObjectiveCard) ServerModel.CARDS_LIST.get(choice1));
        gameController.getCurrentState().pickObjective(game.getPlayers().getLast(), (ObjectiveCard) ServerModel.CARDS_LIST.get(choice2));

        testTarget = game.getCurrentPlayer();

        gameController.getCurrentState().placeCard(game.getCurrentPlayer(), new GenericPair<>(1, 1), game.getCurrentPlayer().getCardsInHand().getFirst(), Side.FRONT);
    }

    /**
     * Tests that drawing from the resource deck during the player's turn is successful.
     */
    @Test
    void successfulDrawFromResourceDeck(){
        assertDoesNotThrow( () -> gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "resource"));
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    /**
     * Tests that drawing from the gold deck during the player's turn is successful.
     */
    @Test
    void successfulDrawFromGoldDeck(){
        assertDoesNotThrow( () -> gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "gold"));
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    /**
     * Tests that drawing from the visible resources is successful.
     */
    @Test
    void successfulDrawFromVisibleResources(){
        assertDoesNotThrow( () -> gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "resource", 0));
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    /**
     * Tests that drawing from the visible golds is successful.
     */
    @Test
    void successfulDrawFromVisibleGolds(){
        assertDoesNotThrow( () -> gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "gold", 1));
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    /**
     * Tests that drawing with the wrong player throws an {@code UnexpectedPlayerException}.
     */
    @Test
    void tryingToDrawWithTheWrongPlayer(){
        assertThrows(UnexpectedPlayerException.class, () -> gameController.getCurrentState().drawFrom(game.getPlayers().getLast(), "Resource"));
    }

    /**
     * Tests that drawing from an invalid position throws an {@code InvalidDeckPositionException}.
     */
    @Test
    void tryingToDrawFromAWrongPosition(){
        assertThrows(InvalidDeckPositionException.class,
                () -> gameController.getCurrentState().drawFrom(
                        game.getCurrentPlayer(), "resource", 3
                )
        );
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnection(){
        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, testTarget.getCardsInHand().size());
        assertInstanceOf(ResourceCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition, but the resource deck is empty so
     * the attempt to draw has to be made on the gold cards deck.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnectionButResourceDeckIsEmpty(){
        while(!game.getResourceCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getResourceCardsDeck().draw());

        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, testTarget.getCardsInHand().size());
        assertInstanceOf(GoldCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition, but the resource deck and gold deck are empty so
     * the attempt to draw has to be made on the visible resource cards.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnectionButAllDecksAreEmpty(){
        while(!game.getResourceCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getResourceCardsDeck().draw());
        while(!game.getGoldCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getGoldCardsDeck().draw());

        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, testTarget.getCardsInHand().size());
        assertNull(game.getPlacedResources()[0]);
        assertInstanceOf(ResourceCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition, but the resource, gold deck and the first visible resource card are empty so
     * the attempt to draw has to be made on the second visible resource cards.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnectionButAllDecksAreEmptyAndAlsoFirstVisibleResource(){
        while(!game.getResourceCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getResourceCardsDeck().draw());
        while(!game.getGoldCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getGoldCardsDeck().draw());
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 0));

        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, testTarget.getCardsInHand().size());
        assertNull(game.getPlacedResources()[1]);
        assertInstanceOf(ResourceCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition, but the resource, gold deck and the visible resource cards are empty so
     * the attempt to draw has to be made on the first visible gold card.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnectionButAllDecksAndVisibleResourcesAreEmpty(){
        while(!game.getResourceCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getResourceCardsDeck().draw());
        while(!game.getGoldCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getGoldCardsDeck().draw());
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 0));
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 1));

        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, testTarget.getCardsInHand().size());
        assertNull(game.getPlacedGolds()[0]);
        assertInstanceOf(GoldCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition, but the resource, gold deck, visible resource cards and the first visible gold card
     * are empty so the attempt to draw has to be made on the second visible gold card.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnectionButAllDecksAndVisibleResourcesAndFirstVisibleGoldsAreEmpty(){
        while(!game.getResourceCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getResourceCardsDeck().draw());
        while(!game.getGoldCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getGoldCardsDeck().draw());
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 0));
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 1));
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedGolds(), 0));

        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, testTarget.getCardsInHand().size());
        assertNull(game.getPlacedGolds()[1]);
        assertInstanceOf(GoldCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    /**
     * Tests the draw routine after a player disconnects and the consequent transition but all the places when an attempt to draw can be made
     * are empty so there's no card to be drawn and the player will stay without a card.
     */
    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnectionButEverythingIsEmpty(){
        while(!game.getResourceCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getResourceCardsDeck().draw());
        while(!game.getGoldCardsDeck().isEmpty())
            assertDoesNotThrow( () -> game.getGoldCardsDeck().draw());
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 0));
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedResources(), 1));
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedGolds(), 0));
        assertDoesNotThrow( () -> game.drawFrom(game.getPlacedGolds(), 1));

        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(2, testTarget.getCardsInHand().size());
        assertInstanceOf(GoldCard.class, testTarget.getCardsInHand().getLast());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }
}