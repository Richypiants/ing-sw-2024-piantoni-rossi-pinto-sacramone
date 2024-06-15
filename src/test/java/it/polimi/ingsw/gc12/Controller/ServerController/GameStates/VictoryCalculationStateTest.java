package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.EndGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.SetLobbiesCommand;
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
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class VictoryCalculationStateTest {

    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    ServerController server;
    ChooseObjectiveCardsState state;
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

    //FIXME: questo test è inutile ma non c'e altro modo di testare un metodo che non viene mai chiamato nel flusso di gioco e non fa nulla
    @Test
    void DisconnectPlayer() throws Exception {
        VictoryCalculationState state = new VictoryCalculationState(gameController, game);
        state.playerDisconnected(game.getPlayers().getFirst());
    }


}