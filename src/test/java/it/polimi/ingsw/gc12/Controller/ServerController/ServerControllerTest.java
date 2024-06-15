package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.PauseGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerControllerTest {

    static NetworkSession nonParticipantPlayer;
    static NetworkSession notExistingPlayer;
    static ConnectionController connectionController = ConnectionController.getInstance();

    LobbyController lobbyController = new LobbyController(null);
    GameController gameController = new GameController(new Game(new Lobby(null, new Player("creator"), 2)));

        /**
     * Creates a NetworkSession stub for testing purposes.
     *
     * @param controller            the controller associated to the VirtualClient contained in the NetworkSession
     * @return a new NetworkSession instance with a custom listener
     */
    public static <T extends ServerController> NetworkSession createNetworkSessionStub(T controller){
        NetworkSession session = new NetworkSession(controller) {

            @Override
            protected NetworkListener createListener(NetworkSession session) {
                return new ServerListener(
                        session,
                        new VirtualClientImpl()
                );
            }
        };
        session.scheduleTimeoutTimerTask(new TimerTask() {
            @Override
            public void run() {
            }

            ;
        });


        return session;
    };

    /**
     * Tests that hasNoPlayer method returns true when the player is not present
     * and the related exceptions are sent to the VirtualClient.
     */
    @Test
    void hasNoPlayerReturningTrue(){
        assertTrue(connectionController.hasNoPlayer(notExistingPlayer));
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (notExistingPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                NotExistingPlayerException.class,
                ((VirtualClientImpl) ((ServerListener) (notExistingPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

/**
     * A stub implementation of the ClientControllerInterface used for testing.
     * It contains an attribute exposing the exception for checking its type during executions
     */
    public static class ClientControllerInterfaceImpl implements ClientControllerInterface {
        public List<Integer> receivedObjectiveIDs;
        public UUID receivedUUID;
        public Exception receivedException = null;
        public List<Integer> lastReceivedCardIDs;
        public String receivedMessage;

        @Override
        public void throwException(Exception e) {
                receivedException = e;
        }

        @Override
        public void setNickname(String nickname) {

        }

        @Override
        public void restoreGame(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD) {

        }

        @Override
        public void setLobbies(Map<UUID, Lobby> lobbies) {

        }

        @Override
        public void updateLobby(Lobby lobby) {
            receivedUUID = lobby.getRoomUUID();
        }

        @Override
        public void startGame(ClientGame gameDTO) {

        }

        @Override
        public void confirmObjectiveChoice(int cardID) {

        }

        @Override
        public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide, EnumMap<Resource, Integer> ownedResources, List<GenericPair<Integer, Integer>> openCorners, int points) {

        }

        @Override
        public void receiveObjectiveChoice(List<Integer> cardIDs) {
            receivedObjectiveIDs = cardIDs;
        }

        @Override
        public void receiveCard(List<Integer> cardIDs) { lastReceivedCardIDs = cardIDs; }

        @Override
        public void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements) {

        }

        @Override
        public void toggleActive(String nickname) {

        }

        @Override
        public void transition(int round, int currentPlayerIndex) {

        }

        @Override
        public void pauseGame() {

        }

        @Override
        public void endGame(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections) {

        }

        @Override
        public void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate) {
            receivedMessage = chatMessage;
        }
    }

    /**
     * Initializes sessions of VirtualClients simulating players in different conditions before any tests are run.
     */
    @BeforeAll
    static void initializingSessions(){
        nonParticipantPlayer = createNetworkSessionStub(connectionController);
        notExistingPlayer = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(nonParticipantPlayer, "thePlayer");
    }

    /**
     * Tests that a keepAlive call is successfully made by a correctly registered player and a new TimerTask is created and
     * linked to his NetworkSession.
     */
    @Test
    void keepAliveSuccessfullyReceived(){
        NetworkSession correctlyRegisteredPlayer = createNetworkSessionStub(connectionController);
        connectionController.createPlayer(correctlyRegisteredPlayer, "Player");

        connectionController.keepAlive(correctlyRegisteredPlayer);

        assertInstanceOf(TimerTask.class, correctlyRegisteredPlayer.getTimeoutTask());
    }

    /**
     * Tests that hasNoPlayer method returns false when the player is present.
     */
    @Test
    void hasNoPlayerReturningFalse(){
        assertFalse(connectionController.hasNoPlayer(nonParticipantPlayer));
    }

    /**
     * Tests that an invalid call to placeCard triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToPlaceCard() {
        connectionController.placeCard(nonParticipantPlayer, new GenericPair<>(1, 1), 1, Side.FRONT);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /*
     * The following section contains
     * tests that verifies that it isn't possible
     * to successfully execute the action implemented by the game controller while assigned to the connection/lobby controller
     * So, every following test is a call sent from a wrong controller and a negative response is expected.
     */

    /**
     * Tests that an invalid call to leaveLobby triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToLeaveLobby() {
        connectionController.leaveLobby(nonParticipantPlayer, true);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to pickObjective triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToPickObjective(){
        connectionController.pickObjective(nonParticipantPlayer, 1);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to drawFromDeck triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToDrawFromDeck() {
        connectionController.drawFromDeck(nonParticipantPlayer, "resource");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to drawFromVisibleCards triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToDrawFromVisibleCards() {
        connectionController.drawFromVisibleCards(nonParticipantPlayer, "resource", 1);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to leaveGame triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToLeaveGame(){
        connectionController.leaveGame(nonParticipantPlayer);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to broadcastMessage triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToBroadcastMessage() {
        connectionController.broadcastMessage(nonParticipantPlayer, "HELLO");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to directMessage triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToDirectMessage() {
        connectionController.directMessage(nonParticipantPlayer, "paolo", "HELLO");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to generatePlayer triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToGeneratePlayer() {
        lobbyController.generatePlayer(nonParticipantPlayer, "Username");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to setNickname triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToSetNickname() {
        gameController.setNickname(nonParticipantPlayer, "Username");
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to createLobby triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToCreateLobby() {
        gameController.createLobby(nonParticipantPlayer, 4);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to joinLobby triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallToJoinLobby() {
        gameController.joinLobby(nonParticipantPlayer, UUID.randomUUID());
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /**
     * Tests that an invalid call to pickColor triggers a ForbiddenActionException.
     */
    @Test
    void invalidCallPickColor() {
        gameController.pickColor(nonParticipantPlayer, Color.RED);
        assertInstanceOf(
                ThrowExceptionCommand.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient()).lastCommandReceived
        );
        assertInstanceOf(
                ForbiddenActionException.class,
                ((VirtualClientImpl) ((ServerListener) (nonParticipantPlayer.getListener())).getVirtualClient())
                        .myClientController.receivedException
        );
    }

    /*
     * The following section contains
     * tests that verifies that it isn't possible
     * to successfully execute the action implemented by the lobby controller while assigned to the connection/game controller
     * So, every following test is a call sent from a wrong controller and a negative response is expected.
     */

    /**
     * A stub implementation of the VirtualClient used for testing.
     */
    public static class VirtualClientImpl implements VirtualClient {

        public ClientControllerInterfaceImpl myClientController = new ClientControllerInterfaceImpl();
        public ClientCommand lastCommandReceived = null;
        public List<ClientCommand> receivedCommandsList = new ArrayList<>();


        @Override
        public void requestToClient(ClientCommand command) {
            lastCommandReceived = command;
            receivedCommandsList.add(command);
            command.execute(myClientController);
            if (command instanceof PauseGameCommand) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }


    //TODO: Complete testing with the missing methods
}