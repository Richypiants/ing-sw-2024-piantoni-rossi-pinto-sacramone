package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ConnectionControllerTest {
    static NetworkSession inLobbyPlayer;
    static NetworkSession inLobbyPlayer2;
    static ConnectionController connectionController = ConnectionController.getInstance();

    LobbyController lobbyController = new LobbyController(null);
    GameController gameController = new GameController(null);

    @BeforeAll
    static void initializingSessions() {
        inLobbyPlayer = createNetworkSessionStub(connectionController, virtualClient);
        inLobbyPlayer2 = createNetworkSessionStub(connectionController, virtualClient);

        connectionController.generatePlayer(inLobbyPlayer, "thePlayer");
        connectionController.generatePlayer(inLobbyPlayer2, "thePlayer2");

    }

    @Test
    void alreadyTakenNickName() {
        connectionController.setNickname(inLobbyPlayer2, "thePlayer2");
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(IllegalArgumentException.class, clientController.receivedException);
    }

    @Test
    void invalidRangeOfMaximumPlayer() {
        connectionController.createLobby(inLobbyPlayer, 5);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(IllegalArgumentException.class, clientController.receivedException);
    }

    @Test
    void notExistingUUID() {
        connectionController.joinLobby(inLobbyPlayer, new UUID(1, 1));
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(IllegalArgumentException.class, clientController.receivedException);
    }
}