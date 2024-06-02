package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SetupStateTest {

    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    ServerController server;
    GameController gameController;
    SetupState state;


    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);

        UUID lobbyUUID = UUID.randomUUID();

        gameController = new GameController(game);
        ServerController.model.GAME_CONTROLLERS.put(lobbyUUID, gameController);

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

        ServerController.activePlayers.put(client1, game.getPlayers().get(0));
        ServerController.activePlayers.put(client2, game.getPlayers().get(1));
        state = new SetupState(gameController, game);
    }

    @Test
    void correctTransitionTest() {
        state.transition();
        assertInstanceOf(ChooseInitialCardsState.class, gameController.getCurrentState());
    }

    @Test
    void correctSetupAfterTransitionTest() {
        state.transition();
        for (var target : game.getPlayers()) {
            assert (!target.getCardsInHand().isEmpty());
            assertInstanceOf(InitialCard.class, target.getCardsInHand().getFirst());

        }
    }

}