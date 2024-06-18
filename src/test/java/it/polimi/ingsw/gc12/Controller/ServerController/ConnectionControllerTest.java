package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.VirtualClientImpl;
import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ConnectionControllerTest {
    static NetworkSession inLobbyPlayer;
    static NetworkSession inLobbyPlayer2;
    static NetworkSession unregisteredClient;
    static NetworkSession notInLobbyPlayer;
    static ConnectionController connectionController = ConnectionController.getInstance();

    @BeforeAll
    static void initializingSessions() {
        inLobbyPlayer = createNetworkSessionStub(connectionController);
        inLobbyPlayer2 = createNetworkSessionStub(connectionController);
        unregisteredClient = createNetworkSessionStub(connectionController);
        notInLobbyPlayer = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(inLobbyPlayer, "thePlayer");
        connectionController.generatePlayer(inLobbyPlayer2, "thePlayer2");
        connectionController.generatePlayer(notInLobbyPlayer, "notInLobbyPlayer");
    }

    @Test
    void alreadyTakenNickName() {
        connectionController.setNickname(inLobbyPlayer2, "thePlayer2");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer2.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                IllegalArgumentException.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer2.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void invalidRangeOfMaximumPlayer() {
        connectionController.createLobby(inLobbyPlayer, 5);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                IllegalArgumentException.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void notExistingUUID() {
        connectionController.joinLobby(inLobbyPlayer, new UUID(1, 1));
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                IllegalArgumentException.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void unregisteredClientSessionCannotCallCommandsTest() {
        connectionController.setNickname(unregisteredClient, "unregisteredClient");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (unregisteredClient.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(NotExistingPlayerException.class,
                ((VirtualClientImpl) ((ServerListener) (unregisteredClient.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
        connectionController.createLobby(unregisteredClient, 2);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (unregisteredClient.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(NotExistingPlayerException.class,
                ((VirtualClientImpl) ((ServerListener) (unregisteredClient.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
        connectionController.joinLobby(unregisteredClient, UUID.randomUUID());
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (unregisteredClient.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(NotExistingPlayerException.class,
                ((VirtualClientImpl) ((ServerListener) (unregisteredClient.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void errorJoiningFullLobbyTest() {
        connectionController.createLobby(inLobbyPlayer, 2);
        connectionController.joinLobby(inLobbyPlayer, ServerController.MODEL.getLobbiesMap().keySet().stream().findAny().orElseThrow());
        connectionController.joinLobby(notInLobbyPlayer, ServerController.MODEL.getLobbiesMap().keySet().stream().findAny().orElseThrow());
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (notInLobbyPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(FullLobbyException.class,
                ((VirtualClientImpl) ((ServerListener) (notInLobbyPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void correctlyExecutedSetNicknameTest() {
        connectionController.setNickname(notInLobbyPlayer, "newNickname");
        assertInstanceOf(
                SetNicknameCommand.class,
                ((VirtualClientImpl) ((ServerListener) (notInLobbyPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
    }
}