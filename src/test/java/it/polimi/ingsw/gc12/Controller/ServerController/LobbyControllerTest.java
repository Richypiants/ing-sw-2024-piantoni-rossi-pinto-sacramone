package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LobbyControllerTest {
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
    void illegalPickColorChoice() {
        LobbyController lobbyController_built = new LobbyController(new Lobby(null, new Player("creator"), 2));
        lobbyController_built.pickColor(inLobbyPlayer, Color.NO_COLOR);
        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inLobbyPlayer.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(UnavailableColorException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inLobbyPlayer.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctLeaveLobby() {
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
        assertEquals(1, associatedLobbyController.CONTROLLED_LOBBY.getPlayersNumber());
    }
}