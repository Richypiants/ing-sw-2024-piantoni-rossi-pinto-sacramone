package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.EndGameCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.SetLobbiesCommand;
import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VictoryCalculationStateTest {

    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
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

        gameController.CONTROLLED_GAME.addListener(client1.getListener());
        gameController.CONTROLLED_GAME.addListener(client2.getListener());

        gameController.putActivePlayer(client1, game.getPlayers().getFirst());
        gameController.putActivePlayer(client2, game.getPlayers().getLast());

        gameController.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(0, 0), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);
        gameController.getCurrentState().placeCard(game.getPlayers().getLast(), new GenericPair<>(0, 0), game.getPlayers().getLast().getCardsInHand().getFirst(), Side.FRONT);

        int choice1 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst();
        int choice2 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client2.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst();

        gameController.getCurrentState().pickObjective(game.getPlayers().getFirst(), (ObjectiveCard) ServerModel.CARDS_LIST.get(choice1));
        gameController.getCurrentState().pickObjective(game.getPlayers().getLast(), (ObjectiveCard) ServerModel.CARDS_LIST.get(choice2));

    }

    @Test
    void correctTransitionToVictoryCalculationState() throws Exception {
        game.getCurrentPlayer().increasePoints(20);
        gameController.getCurrentState().placeCard(game.getCurrentPlayer(), new GenericPair<>(1, 1), game.getCurrentPlayer().getCardsInHand().getFirst(), Side.FRONT);
        for (int i = 0; i < 7; i++) {
            gameController.getCurrentState().transition();
        }

        List<ClientCommand> receivedCommandsListPlayer1 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).receivedCommandsList;
        List<ClientCommand> receivedCommandsListPlayer2 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client2.getListener()).getVirtualClient()).receivedCommandsList;

        assertInstanceOf(EndGameCommand.class, receivedCommandsListPlayer1.get(receivedCommandsListPlayer1.size() - 2));
        assertInstanceOf(EndGameCommand.class, receivedCommandsListPlayer2.get(receivedCommandsListPlayer2.size() - 2));

        assertInstanceOf(SetLobbiesCommand.class, receivedCommandsListPlayer1.getLast());
        assertInstanceOf(SetLobbiesCommand.class, receivedCommandsListPlayer2.getLast());

    }

    @Test
    void correctLeaderboardOnWinForDisconnections() throws Exception {
        game.getCurrentPlayer().increasePoints(20);
        game.getCurrentPlayer().setPlayerActivity(false);
        gameController.getCurrentState().placeCard(game.getCurrentPlayer(), new GenericPair<>(1, 1), game.getCurrentPlayer().getCardsInHand().getFirst(), Side.FRONT);
        for (int i = 0; i < 7; i++) {
            try {
                gameController.getCurrentState().transition();
            } catch (NoSuchElementException ignored) {
            }
        }

        List<ClientCommand> receivedCommandsListPlayer1 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).receivedCommandsList;
        List<ClientCommand> receivedCommandsListPlayer2 = ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client2.getListener()).getVirtualClient()).receivedCommandsList;

        assertInstanceOf(EndGameCommand.class, receivedCommandsListPlayer1.get(receivedCommandsListPlayer1.size() - 2));
        assertInstanceOf(EndGameCommand.class, receivedCommandsListPlayer2.get(receivedCommandsListPlayer2.size() - 2));

        receivedCommandsListPlayer1.getFirst().execute(
                ((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).myClientController
        );

        assertNotEquals(((ServerControllerTest.VirtualClientImpl) ((ServerListener) client1.getListener()).getVirtualClient()).myClientController.receivedLeaderboard.getFirst().getX(), game.getCurrentPlayer().getNickname());
    }

    @Test
    void disconnectPlayer() {
        VictoryCalculationState state = new VictoryCalculationState(gameController, game);
        state.playerDisconnected(game.getPlayers().getFirst());
    }


}