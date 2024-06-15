package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.ServerModel;
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

class PlayerTurnDrawStateTest {


    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    ServerController server;
    GameController gameController;


    @BeforeEach
    void setGameParameters() throws Exception {
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

        gameController.getCurrentState().placeCard(game.getCurrentPlayer(), new GenericPair<>(1, 1), game.getCurrentPlayer().getCardsInHand().getFirst(), Side.FRONT);


    }

    @Test
    void correctTransitionTest_Draw1() throws Exception {
        gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "Resource");
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    @Test
    void correctTransitionTest_Draw2() throws Exception {
        gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "Resource", 1);
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    @Test
    void correctUnexpectedPlayerExceptionCall() throws Exception {
        assertThrows(UnexpectedPlayerException.class, () -> gameController.getCurrentState().drawFrom(game.getPlayers().getLast(), "Resource"));

    }

    @Test
    void correctInvalidDeckPositionException() throws Exception {
        assertThrows(InvalidDeckPositionException.class,
                () -> gameController.getCurrentState().drawFrom(
                        game.getCurrentPlayer(), "Resource", 3
                )
        );

    }

    @Test
    void correctTransitionsAndDrawRoutineAfterDisconnection() throws Exception {
        gameController.getCurrentState().playerDisconnected(game.getCurrentPlayer());
        assertEquals(3, game.getCurrentPlayer().getCardsInHand().size());
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());

    }




}