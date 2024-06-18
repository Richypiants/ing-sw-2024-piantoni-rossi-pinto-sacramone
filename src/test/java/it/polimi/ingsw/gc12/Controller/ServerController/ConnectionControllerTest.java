package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.VirtualClientImpl;
import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.*;

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
    void runTimeOutTimerTaskWhileConnected(){
        NetworkSession passivePlayer = createNetworkSessionStub(connectionController);
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