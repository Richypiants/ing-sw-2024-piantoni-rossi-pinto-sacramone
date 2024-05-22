package it.polimi.ingsw.gc12.Controller.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.SetupState;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SetupStateTest {

    Player player1;
    Player player2;
    GameLobby lobby;
    Game game;
    VirtualClient client1;
    VirtualClient client2;
    ServerController server;

    SetupState state;


    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        lobby = new GameLobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);

        client1 = command -> {
        };
        client2 = command -> {
        };

        UUID lobbyUUID = UUID.randomUUID();
        ServerController.lobbiesAndGames.put(lobbyUUID, game);
        ServerController.players.put(client1, game.getPlayers().get(0));
        ServerController.players.put(client2, game.getPlayers().get(1));
        GameController gameController = new GameController(game);
        ServerController.playersToControllers.put(game.getPlayers().get(0), gameController);
        ServerController.playersToControllers.put(game.getPlayers().get(1), gameController);
        state = new SetupState(game);
    }

    @Test
    void correctTransitionTest() {
        state.transition();
        assertInstanceOf(ChooseInitialCardsState.class, game.getCurrentState());
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