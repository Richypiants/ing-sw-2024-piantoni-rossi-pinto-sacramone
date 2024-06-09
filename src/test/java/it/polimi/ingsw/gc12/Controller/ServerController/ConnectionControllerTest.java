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

    @BeforeAll
    static void initializingSessions() {
        inLobbyPlayer = createNetworkSessionStub(connectionController);
        inLobbyPlayer2 = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(inLobbyPlayer, "thePlayer");
        connectionController.generatePlayer(inLobbyPlayer2, "thePlayer2");
    }

    @Test
    void alreadyTakenNickName() {
        connectionController.setNickname(inLobbyPlayer2, "thePlayer2");
        assertInstanceOf(ThrowExceptionCommand.class, ((VirtualClientImpl) inLobbyPlayer2.getListener().getVirtualClient()).receivedCommand);
        assertInstanceOf(IllegalArgumentException.class, ((VirtualClientImpl) inLobbyPlayer2.getListener().getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void invalidRangeOfMaximumPlayer() {
        connectionController.createLobby(inLobbyPlayer, 5);
        assertInstanceOf(ThrowExceptionCommand.class, ((VirtualClientImpl) inLobbyPlayer.getListener().getVirtualClient()).receivedCommand);
        assertInstanceOf(IllegalArgumentException.class, ((VirtualClientImpl) inLobbyPlayer.getListener().getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void notExistingUUID() {
        connectionController.joinLobby(inLobbyPlayer, new UUID(1, 1));
        assertInstanceOf(ThrowExceptionCommand.class, ((VirtualClientImpl) inLobbyPlayer.getListener().getVirtualClient()).receivedCommand);
        assertInstanceOf(IllegalArgumentException.class, ((VirtualClientImpl) inLobbyPlayer.getListener().getVirtualClient()).myClientController.receivedException);
    }
}