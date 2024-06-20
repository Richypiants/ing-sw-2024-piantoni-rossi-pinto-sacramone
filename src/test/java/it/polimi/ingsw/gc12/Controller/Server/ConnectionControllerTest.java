package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.VirtualClientImpl;
import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void runTimeOutTimerTaskWhileConnected(){
        NetworkSession passivePlayer = createNetworkSessionStub(connectionController);
        connectionController.generatePlayer(passivePlayer, "passivePlayer");
        connectionController.createTimeoutTask(passivePlayer).run();
        assertFalse(ServerController.MODEL.LOBBIES_LISTENERS.contains(passivePlayer.getListener()));
    }

    @Test
    void alreadyRegisteredPlayer(){
        connectionController.createPlayer(inLobbyPlayer, "registerMe");

        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (inLobbyPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void registerAPlayerWithAnAlreadyExistingNickname(){
        NetworkSession newPlayer = createNetworkSessionStub(connectionController);

        connectionController.createPlayer(newPlayer, inLobbyPlayer.getPlayer().getNickname());

        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (newPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                IllegalArgumentException.class,
                ((VirtualClientImpl) ((ServerListener) (newPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    @Test
    void successfulNicknameChange(){
        NetworkSession newPlayer = createNetworkSessionStub(connectionController);
        connectionController.createPlayer(newPlayer, "name");

        String desiredNickname = "wow!";
        connectionController.setNickname(newPlayer, desiredNickname);

        assertEquals(desiredNickname, newPlayer.getPlayer().getNickname());
    }

    @Test
    void rejoiningWithAnAlreadyConnectedPlayer(){
        NetworkSession previouslyConnectedPlayer = createNetworkSessionStub(connectionController);

        ServerController.INACTIVE_SESSIONS.put("tester", previouslyConnectedPlayer);
        connectionController.createPlayer(previouslyConnectedPlayer, "tester");

        assertFalse(ServerController.INACTIVE_SESSIONS.containsKey(previouslyConnectedPlayer.getPlayer().getNickname()));
    }

    @Test
    void failedToRetrieveAnActivePlayer(){
        assertThrows(NoSuchElementException.class, () -> connectionController.getSessionFromActivePlayer(new Player("invisible")));
    }

}