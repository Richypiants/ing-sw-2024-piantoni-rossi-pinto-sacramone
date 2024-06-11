package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ChooseInitialCardsStateTest {

    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    ServerController server;
    GameController gameController;
    GameState state;


    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        UUID lobbyUUID = UUID.randomUUID();

        lobby = new Lobby(lobbyUUID, player1, 2);
        lobby.addPlayer(player2);

        game = new Game(lobby);
        gameController = GameController.MODEL.createGameController(game);

        client1 = new NetworkSession(gameController) {
            @Override
            protected Listener createListener(NetworkSession session) {
                return new Listener(session, command -> {
                });
            }
        };
        client2 = new NetworkSession(gameController) {
            @Override
            protected Listener createListener(NetworkSession session) {
                return new Listener(session, command -> {
                });
            }
        };

        gameController.putActivePlayer(client1, game.getPlayers().get(0));
        gameController.putActivePlayer(client2, game.getPlayers().get(1));

        state = new ChooseInitialCardsState(gameController, game);
    }

    @Test
    void correctTransitionTest() {
        gameController.getCurrentState().transition();
        assertInstanceOf(ChooseObjectiveCardsState.class, gameController.getCurrentState());
    }

    @Test
    void transitionStartAfterAllCheck() throws Exception {
        for (var target : game.getPlayers()) {
            gameController.getCurrentState().placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            if (target != game.getPlayers().getLast()) {
                assertInstanceOf(ChooseInitialCardsState.class, gameController.getCurrentState());
            } else {
                assertInstanceOf(ChooseObjectiveCardsState.class, gameController.getCurrentState());
            }
        }

    }
}