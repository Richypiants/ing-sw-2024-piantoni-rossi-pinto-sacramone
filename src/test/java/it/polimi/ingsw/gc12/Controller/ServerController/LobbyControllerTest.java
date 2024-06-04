package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.virtualClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LobbyControllerTest {
    static NetworkSession inLobbyPlayer;
    static NetworkSession inLobbyPlayer2;
    static ConnectionController connectionController = ConnectionController.getInstance();

    LobbyController lobbyController = new LobbyController(null);
    GameController gameController = new GameController(null);
    ServerControllerTest.ClientControllerInterfaceImpl clientController = ServerControllerTest.clientController;


    @BeforeAll
    static void initializingSessions() {
        inLobbyPlayer = createNetworkSessionStub(connectionController, virtualClient);
        inLobbyPlayer2 = createNetworkSessionStub(connectionController, virtualClient);

        connectionController.generatePlayer(inLobbyPlayer, "thePlayer");
        connectionController.generatePlayer(inLobbyPlayer2, "thePlayer2");

    }

    @Test
    void illegalPickColorChoice() {
        LobbyController lobbyController_built = new LobbyController(new Lobby(new Player("creator"), 2));
        lobbyController_built.pickColor(inLobbyPlayer, Color.NO_COLOR);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(UnavailableColorException.class, clientController.receivedException);
    }

    @Test
    void correctLeaveLobby() {
        NetworkSession passivePlayer = createNetworkSessionStub(connectionController, virtualClient);
        NetworkSession lobbyCreatorPlayer = createNetworkSessionStub(connectionController, virtualClient);
        NetworkSession joiningPlayer = createNetworkSessionStub(connectionController, virtualClient);

        connectionController.generatePlayer(passivePlayer, "passivePlayer");
        connectionController.generatePlayer(lobbyCreatorPlayer, "lobbyCreatorPlayer");
        connectionController.generatePlayer(joiningPlayer, "joiningPlayer");

        connectionController.createLobby(lobbyCreatorPlayer, 2);
        connectionController.joinLobby(joiningPlayer, clientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) lobbyCreatorPlayer.getController();

        associatedLobbyController.leaveLobby(lobbyCreatorPlayer, true);
        assertEquals(1, associatedLobbyController.CONTROLLED_LOBBY.getPlayersNumber());

    }
}