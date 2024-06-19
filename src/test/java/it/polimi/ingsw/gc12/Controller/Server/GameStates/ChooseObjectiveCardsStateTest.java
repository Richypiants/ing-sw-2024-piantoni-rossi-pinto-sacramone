package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Controller.Server.ServerController;
import it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.*;

class ChooseObjectiveCardsStateTest {

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

        gameController.putActivePlayer(client1, game.getPlayers().get(0));
        gameController.putActivePlayer(client2, game.getPlayers().get(1));
    }

    @Test
    void correctTransitionTest() throws Exception {
        for (var target : game.getPlayers()) {
            gameController.getCurrentState().placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
        }
        gameController.getCurrentState().transition();
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }

    @Test
    void successfulPickObjective(){
        InGamePlayer target = game.getPlayers().getFirst();
        target.addListener(client1.getListener());

        gameController.setState(new ChooseInitialCardsState(gameController, game));
        gameController.getCurrentState().transition();

        int receivedObjectiveCardID = ((ServerControllerTest.VirtualClientImpl)
                ((ServerListener) client1.getListener()).getVirtualClient()).myClientController
                .receivedObjectiveIDs.getFirst();
        assertDoesNotThrow(() -> gameController.getCurrentState().pickObjective( target,
                (ObjectiveCard) ServerModel.CARDS_LIST.get(receivedObjectiveCardID)));
        assertNotNull(target.getSecretObjective());
    }

    @Test
    void attemptToPickObjectiveWithoutOwningTheCard(){
        InGamePlayer target = game.getPlayers().getFirst();
        target.addListener(client1.getListener());

        gameController.setState(new ChooseInitialCardsState(gameController, game));
        gameController.getCurrentState().transition();

        assertThrows(CardNotInHandException.class, () -> gameController.getCurrentState().pickObjective( target,
                (ObjectiveCard) ServerModel.CARDS_LIST.get(0)));

        assertNull(target.getSecretObjective());
    }

    @Test
    void attemptToPickObjectiveTwice(){
        InGamePlayer target = game.getPlayers().getFirst();
        target.addListener(client1.getListener());

        gameController.setState(new ChooseInitialCardsState(gameController, game));
        gameController.getCurrentState().transition();

        int firstChoiceID = ((ServerControllerTest.VirtualClientImpl)
                ((ServerListener) client1.getListener()).getVirtualClient()).myClientController
                .receivedObjectiveIDs.getFirst();
        int secondChoiceID = ((ServerControllerTest.VirtualClientImpl)
                ((ServerListener) client1.getListener()).getVirtualClient()).myClientController
                .receivedObjectiveIDs.get(1);
        assertDoesNotThrow( () -> gameController.getCurrentState().pickObjective( target,
                (ObjectiveCard) ServerModel.CARDS_LIST.get(firstChoiceID)));

        assertThrows(AlreadySetCardException.class , () -> gameController.getCurrentState().pickObjective( target,
                (ObjectiveCard) ServerModel.CARDS_LIST.get(secondChoiceID)));

        assertNotNull(target.getSecretObjective());
    }

    @Test
    void correctPickCardIfPlayerDisconnected() throws Exception {
        gameController.CONTROLLED_GAME.getPlayers().getFirst().addListener(client1.getListener());
        gameController.CONTROLLED_GAME.getPlayers().getLast().addListener(client2.getListener());

        gameController.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(0, 0), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);
        gameController.getCurrentState().placeCard(game.getPlayers().getLast(), new GenericPair<>(0, 0), game.getPlayers().getLast().getCardsInHand().getFirst(), Side.FRONT);

        gameController.getCurrentState().playerDisconnected(game.getPlayers().getFirst());
        assertNotNull(gameController.CONTROLLED_GAME.getPlayers().getFirst().getSecretObjective());
    }



}