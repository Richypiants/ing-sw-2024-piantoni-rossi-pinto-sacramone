package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ChooseInitialCardsStateTest {

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

        server = server.getInstance();
        client1 = new VirtualClient() {
            @Override
            public void requestToClient(ClientCommand command) throws Exception {

            }
        };

        client2 = new VirtualClient() {
            @Override
            public void requestToClient(ClientCommand command) throws Exception {

            }
        };
        UUID lobbyUUID = UUID.randomUUID();
        server.lobbiesAndGames.put(lobbyUUID, game);
        server.players.put(client1, game.getPlayers().get(0));
        server.players.put(client2, game.getPlayers().get(1));
        server.playersToLobbiesAndGames.put(game.getPlayers().get(0), game);
        server.playersToLobbiesAndGames.put(game.getPlayers().get(1), game);

        state = new SetupState(game);
        state.transition();
    }

    @Test
    void correctTransitionTest() {
        game.getCurrentState().transition();
        assertInstanceOf(ChooseObjectiveCardsState.class, game.getCurrentState());
    }

    @Test
    void transitionStartAfterAllcheck() throws Exception {
        for (var target : game.getPlayers()) {
            game.getCurrentState().placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            if (target != game.getPlayers().getLast()) {
                assertInstanceOf(ChooseInitialCardsState.class, game.getCurrentState());
            } else {
                assertInstanceOf(ChooseObjectiveCardsState.class, game.getCurrentState());
            }
        }

    }
}