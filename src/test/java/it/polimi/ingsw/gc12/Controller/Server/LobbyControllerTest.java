package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.Server.Server;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LobbyControllerTest {
    static NetworkSession inLobbyPlayer;
    static NetworkSession inLobbyPlayer2;
    ConnectionController connectionController = ConnectionController.getInstance();

    @Test
    void illegalPickColorChoice() {
        inLobbyPlayer = createNetworkSessionStub(connectionController);
        inLobbyPlayer2 = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(inLobbyPlayer, "thePlayer");
        connectionController.generatePlayer(inLobbyPlayer2, "thePlayer2");

        LobbyController lobbyController_built = new LobbyController(new Lobby(null, new Player("creator"), 2));
        lobbyController_built.pickColor(inLobbyPlayer, Color.NO_COLOR);
        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inLobbyPlayer.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnavailableColorException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inLobbyPlayer.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctLeaveLobby() throws InterruptedException {
        NetworkSession passivePlayer = createNetworkSessionStub(connectionController);
        NetworkSession lobbyCreatorPlayer = createNetworkSessionStub(connectionController);
        NetworkSession joiningPlayer = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(passivePlayer, "passivePlayer");
        connectionController.generatePlayer(lobbyCreatorPlayer, "lobbyCreatorPlayer");
        connectionController.generatePlayer(joiningPlayer, "joiningPlayer");

        connectionController.createLobby(lobbyCreatorPlayer, 2);
        connectionController.joinLobby(joiningPlayer, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) joiningPlayer.getListener()).getVirtualClient()).myClientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) lobbyCreatorPlayer.getController();

        associatedLobbyController.leaveLobby(lobbyCreatorPlayer, true);

        synchronized (this) {
            wait(10);
        }

        assertEquals(1, associatedLobbyController.CONTROLLED_LOBBY.getPlayersNumber());
    }

    @Test
    public void testLeaveLobby_WhenActive() throws InterruptedException {
        boolean isInactive = false;
        NetworkSession lobbyCreatorPlayer = createNetworkSessionStub(connectionController);
        NetworkSession joiningPlayer = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(lobbyCreatorPlayer, "alpha");
        connectionController.generatePlayer(joiningPlayer, "beta");

        connectionController.createLobby(lobbyCreatorPlayer, 2);
        connectionController.joinLobby(joiningPlayer, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) joiningPlayer.getListener()).getVirtualClient()).myClientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) lobbyCreatorPlayer.getController();

        Server.getInstance().commandExecutorsPool.submit(() -> {
            associatedLobbyController.leaveLobby(joiningPlayer, isInactive);
        });

        synchronized (this) {
            wait(10);
        }

        assertInstanceOf(ConnectionController.class, joiningPlayer.getController());
        //assertFalse(associatedLobbyController.CONTROLLED_LOBBY.getPlayers().contains(joiningPlayer.getPlayer()));
        //assertEquals(1, associatedLobbyController.CONTROLLED_LOBBY.getPlayers().size());
    }

    @Test
    void runTimeOutTimerTaskWhileInALobby() throws InterruptedException {
        NetworkSession lobbyCreatorPlayer = createNetworkSessionStub(connectionController);
        NetworkSession joiningPlayer = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(lobbyCreatorPlayer, "gamma");
        connectionController.generatePlayer(joiningPlayer, "delta");

        connectionController.createLobby(lobbyCreatorPlayer, 2);
        connectionController.joinLobby(joiningPlayer, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) joiningPlayer.getListener()).getVirtualClient()).myClientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) lobbyCreatorPlayer.getController();

        assertInstanceOf(LobbyController.class, joiningPlayer.getController());

        Server.getInstance().commandExecutorsPool.submit(() -> {
            associatedLobbyController.createTimeoutTask(joiningPlayer).run();
        });

        synchronized (this) {
            wait(10);
        }

        assertInstanceOf(ConnectionController.class, joiningPlayer.getController());
        //assertFalse( associatedLobbyController.CONTROLLED_LOBBY.getPlayers().contains(joiningPlayer.getPlayer()));
        //assertEquals(1, associatedLobbyController.CONTROLLED_LOBBY.getPlayersNumber());
    }

}