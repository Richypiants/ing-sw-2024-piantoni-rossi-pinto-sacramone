package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.*;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.*;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServerControllerTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    ClientGame clientGame;
    static NetworkSession nonParticipantPlayer;
    static NetworkSession notExistingPlayer;
    NetworkSession inTurnPlayer;
    NetworkSession NotInTurnPlayer;
    ChooseObjectiveCardsState state;

    static ConnectionController connectionController = ConnectionController.getInstance();
    LobbyController lobbyController = new LobbyController(null);
    GameController gameController;

    static class ClientControllerInterfaceImpl implements ClientControllerInterface {

        public Exception receivedException = null;

        @Override
        public void throwException(Exception e) {
                receivedException = e;
        }

        @Override
        public void keepAlive() {

        }

        @Override
        public void setNickname(String nickname) {

        }

        @Override
        public void restoreGame(UUID gameUUID, ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD) {

        }

        @Override
        public void setLobbies(Map<UUID, Room> lobbies) {

        }

        @Override
        public void updateLobby(UUID lobbyUUID, Lobby lobby) {

        }

        @Override
        public void startGame(UUID lobbyUUID, ClientGame gameDTO) {

        }

        @Override
        public void confirmObjectiveChoice(int cardID) {

        }

        @Override
        public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide, EnumMap<Resource, Integer> ownedResources, List<GenericPair<Integer, Integer>> openCorners, int points) {

        }

        @Override
        public void receiveObjectiveChoice(List<Integer> cardIDs) {

        }

        @Override
        public void receiveCard(List<Integer> cardIDs) {

        }

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

        }
    };

    static class VirtualClientImpl implements VirtualClient {

        public ClientCommand receivedCommand = null;

        @Override
        public void requestToClient(ClientCommand command) {
            receivedCommand = command;
            command.execute(clientController);
        }
    }

    static ClientControllerInterfaceImpl clientController = new ClientControllerInterfaceImpl();
    static VirtualClientImpl virtualClient = new VirtualClientImpl();

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        goldCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.GOLD_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
        objectiveCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.OBJECTIVE_DECK_FILENAME, new TypeToken<>(){});
    }

    public static <T extends ServerController> NetworkSession createNetworkSessionStub(T controller, VirtualClient redefinedVirtualClient){
        return new NetworkSession(controller) {
            @Override
            protected Listener createListener(NetworkSession session) {
                return new Listener(
                        session,
                        redefinedVirtualClient
                );
            }
        };
    }

    @BeforeAll
    static void initializingSessions(){
        nonParticipantPlayer = createNetworkSessionStub(connectionController, virtualClient);
        notExistingPlayer = createNetworkSessionStub(connectionController, virtualClient);
    }

    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("Sacri");
        player2 = new Player("Piants");
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);
        ConnectionController controller = ConnectionController.getInstance();
        UUID lobbyUUID = UUID.randomUUID();

        gameController = new GameController(game);
        ServerController.model.GAME_CONTROLLERS.put(lobbyUUID, gameController);

        inTurnPlayer = new NetworkSession(gameController) {
            @Override
            protected Listener createListener(NetworkSession session) {
                return new Listener(session, command -> {
                });
            }
        };
        NotInTurnPlayer = new NetworkSession(gameController) {
            @Override
            protected Listener createListener(NetworkSession session) {
                return new Listener(session, command -> {
                });
            }
        };

        ServerController.activePlayers.put(inTurnPlayer, game.getPlayers().get(0));
        ServerController.activePlayers.put(NotInTurnPlayer, game.getPlayers().get(1));

        gameController.getCurrentState().transition();

        int i = 0;
        for (var target : game.getPlayers()) {
            target.placeCard(new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            target.addCardToHand(resourceCards.get(i));
            i++;
            target.addCardToHand(resourceCards.get(i));
            target.addCardToHand(goldCards.get(i));
            i++;
        }

        Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesMap = new HashMap<>();
        ArrayList<ObjectiveCard> obj_a = new ArrayList<>();
        obj_a.add(objectiveCards.getFirst());
        obj_a.add(objectiveCards.get(1));

        ArrayList<ObjectiveCard> obj_a2 = new ArrayList<>();
        obj_a2.add(objectiveCards.get(2));
        obj_a2.add(objectiveCards.get(3));

        objectivesMap.put(game.getPlayers().getFirst(), obj_a);
        objectivesMap.put(game.getPlayers().getLast(), obj_a2);

        state = new ChooseObjectiveCardsState(gameController, game, objectivesMap);

        for (var target : game.getPlayers()) {
            state.pickObjective(target, objectivesMap.get(target).getFirst());
        }

        gameController.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(1, 1), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);
    }

    /**
     * hasPlayer() helper method
     */

    @Test
    void keepAliveSuccessfullyReceived(){
        NetworkSession correctlyRegisteredPlayer = createNetworkSessionStub(connectionController, virtualClient);
        connectionController.createPlayer(correctlyRegisteredPlayer, "Player");

        connectionController.keepAlive(correctlyRegisteredPlayer);

        assertInstanceOf(TimerTask.class, correctlyRegisteredPlayer.getTimeoutTask());
    }

    @Test
    void hasNoPlayerReturningFalse(){
        assertFalse(gameController.hasNoPlayer(inTurnPlayer));
    }

    @Test
    void hasNoPlayerReturningTrue(){
        assertTrue(connectionController.hasNoPlayer(notExistingPlayer));
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(NotExistingPlayerException.class, clientController.receivedException);
    }

    /**
     * Subgroup of test that verifies that it isn't possible
     * to successfully execute the action implemented by the game controller while assigned to the connection controller
     */


    @Test
    void invalidCallToPlaceCard() {
        connectionController.placeCard(nonParticipantPlayer, new GenericPair<>(1, 1), 1, Side.FRONT);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToLeaveLobby() {
        connectionController.leaveLobby(nonParticipantPlayer, true);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToPickObjective(){
        connectionController.pickObjective(nonParticipantPlayer, 1);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToDrawFromDeck() {
        connectionController.drawFromDeck(nonParticipantPlayer, "resource");
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToDrawFromVisibleCards() {
        connectionController.drawFromVisibleCards(nonParticipantPlayer, "resource", 1);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToLeaveGame(){
        connectionController.leaveGame(nonParticipantPlayer);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToBroadcastMessage() {
        connectionController.broadcastMessage(nonParticipantPlayer, "HELLO");
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToDirectMessage() {
        connectionController.directMessage(nonParticipantPlayer, "paolo", "HELLO");
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    /**
     * Subgroup of test that verifies that it isn't possible
     * to successfully execute the action implemented by the connection controller while assigned to the game/lobby controller
     */

    @Test
    void invalidCallToGeneratePlayer() {
        lobbyController.generatePlayer(nonParticipantPlayer, "Username");
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToSetNickname() {
        gameController.setNickname(nonParticipantPlayer, "Username");
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToCreateLobby() {
        gameController.createLobby(nonParticipantPlayer, 4);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }

    @Test
    void invalidCallToJoinLobby() {
        gameController.joinLobby(nonParticipantPlayer, UUID.randomUUID());
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }
    /**
     * Subgroup of test that verifies that it isn't possible
     * to successfully execute the action implemented by the lobby controller while assigned to the game/connection controller
     */

    @Test
    void invalidCallPickColor() {
        gameController.pickColor(nonParticipantPlayer, Color.RED);
        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(ForbiddenActionException.class, clientController.receivedException);
    }
}