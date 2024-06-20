package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Controller.Server.ServerController;
import it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.*;

class AwaitingReconnectionStateTest {
    Player player1;
    Player player2;
    Lobby lobby;
    UUID lobbyID;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    GameController gameController;
    AwaitingReconnectionState awaitingReconnectionState;

    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        lobbyID = UUID.randomUUID();

        lobby = new Lobby(lobbyID, player1, 2);
        lobby.addPlayer(player2);

        game = new Game(lobby);
        gameController = GameController.MODEL.createGameController(game);

        client1 = createNetworkSessionStub(gameController);
        client2 = createNetworkSessionStub(gameController);

        client1.setPlayer(gameController.CONTROLLED_GAME.getPlayers().getFirst());
        client2.setPlayer(gameController.CONTROLLED_GAME.getPlayers().getLast());

        gameController.CONTROLLED_GAME.getPlayers().getFirst().addListener(client1.getListener());
        gameController.CONTROLLED_GAME.getPlayers().getLast().addListener(client2.getListener());

        gameController.CONTROLLED_GAME.addListener(client1.getListener());
        gameController.CONTROLLED_GAME.addListener(client2.getListener());

        gameController.putActivePlayer(client1, player1);
        gameController.putActivePlayer(client1, player1);

        gameController.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(0, 0), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);
        gameController.getCurrentState().placeCard(game.getPlayers().getLast(), new GenericPair<>(0, 0), game.getPlayers().getLast().getCardsInHand().getFirst(), Side.FRONT);

        int choice1 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst();
        int choice2 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client2.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst();

        gameController.getCurrentState().pickObjective(game.getPlayers().getFirst(), (ObjectiveCard) ServerModel.CARDS_LIST.get(choice1));
        gameController.getCurrentState().pickObjective(game.getPlayers().getLast(), (ObjectiveCard) ServerModel.CARDS_LIST.get(choice2));
    }

    @Test
    void correctStateStored(){
        GameState lastCurrentState = gameController.getCurrentState();
        awaitingReconnectionState = new AwaitingReconnectionState(gameController, game);

        awaitingReconnectionState.transition();
        assertEquals(lastCurrentState, gameController.getCurrentState());
    }

    @Test
    void cancelTermination(){
        awaitingReconnectionState = new AwaitingReconnectionState(gameController, game);
        awaitingReconnectionState.cancelTimerTask();
    }

    @Test
    void correctDisconnection(){
        awaitingReconnectionState = new AwaitingReconnectionState(gameController, game);

        awaitingReconnectionState.playerDisconnected(game.getPlayers().getLast());
        assertNull(ServerController.MODEL.getGameController(lobbyID));
    }

    //TESTING THE PREDEFINED FORBIDDEN ACTIONS IN GameState
    @Test
    void tryingToPerformForbiddenActionsInThisState(){
        gameController.setState(new AwaitingReconnectionState(gameController, game));

        assertThrows(ForbiddenActionException.class ,
                () -> gameController.getCurrentState().pickObjective(
                        game.getPlayers().getFirst(),
                        (ObjectiveCard) ServerModel.CARDS_LIST.get(100)
                ));

        assertThrows(ForbiddenActionException.class ,
                () -> gameController.getCurrentState().drawFrom(
                        game.getPlayers().getFirst(),
                        "resource"
                ));

        assertThrows(ForbiddenActionException.class ,
                () -> gameController.getCurrentState().drawFrom(
                        game.getPlayers().getFirst(), "resource",0));


        assertThrows(ForbiddenActionException.class ,
                () -> gameController.getCurrentState().placeCard(
                        game.getPlayers().getFirst(),
                        new GenericPair<>(0,0),
                        (PlayableCard) ServerModel.CARDS_LIST.get(1),
                        Side.FRONT
                ));
    }
}