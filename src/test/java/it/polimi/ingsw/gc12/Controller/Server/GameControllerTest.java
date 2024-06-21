package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.RestoreGameCommand;
import it.polimi.ingsw.gc12.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Listeners.Server.ServerListener;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.*;


class GameControllerTest {

    public static <T extends ServerController> NetworkSession createNetworkSessionStub2(T controller) {
        NetworkSession session = new NetworkSession(controller) {

            @Override
            protected NetworkListener createListener(NetworkSession session) {
                return new ServerListener(
                        session,
                        new ServerControllerTest.VirtualClientImpl()
                );
            }

            @Override
            public void close() {
            }
        };
        session.scheduleTimeoutTimerTask(new TimerTask() {
            @Override
            public void run() {
            }

            ;
        });

        return session;
    }

    ;

    static NetworkSession inGamePlayer_1;
    static NetworkSession inGamePlayer_2;
    static ConnectionController connectionController = ConnectionController.getInstance();;

    static GameController gameAssociatedController;

    @BeforeEach
    void initializingSessions() {
        inGamePlayer_1 = createNetworkSessionStub2(connectionController);
        inGamePlayer_2 = createNetworkSessionStub2(connectionController);

        connectionController.generatePlayer(inGamePlayer_1, "thePlayer");
        connectionController.generatePlayer(inGamePlayer_2, "thePlayer_2");

        connectionController.createLobby(inGamePlayer_1, 2);
        connectionController.joinLobby(inGamePlayer_2, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) inGamePlayer_1.getController();
        associatedLobbyController.pickColor(inGamePlayer_1, Color.RED);
        associatedLobbyController.pickColor(inGamePlayer_2, Color.GREEN);

        gameAssociatedController = (GameController) inGamePlayer_1.getController();
        gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(0, 0), ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.lastReceivedCardID, Side.FRONT);
        gameAssociatedController.placeCard(inGamePlayer_2, new GenericPair<>(0, 0), ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.lastReceivedCardID, Side.FRONT);
    }

    private void doPickObjectivesSelection(){
        gameAssociatedController.pickObjective(inGamePlayer_1, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());
        gameAssociatedController.pickObjective(inGamePlayer_2, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getLast());
    }


    @Test
    void correctCallToCardNotInHand() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), 84, Side.BACK);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(CardNotInHandException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToNotEnoughResources() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().get(2).ID, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(NotEnoughResourcesException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToInvalidPosition() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(3, 3), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(InvalidCardPositionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnexpectedPlayerPlay() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerNotInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerNotInTurn = inGamePlayer_2;
        else
            currentPlayerNotInTurn = inGamePlayer_1;

        gameAssociatedController.placeCard(currentPlayerNotInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnexpectedPlayerDrawDeck() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        NetworkSession currentPlayerNotInTurn;

        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerNotInTurn = inGamePlayer_2;
        else
            currentPlayerNotInTurn = inGamePlayer_1;

        gameAssociatedController.drawFromDeck(currentPlayerNotInTurn, "resource");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnknownStringDeck() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromDeck(currentPlayerInTurn, "Deck");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnknownStringException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToEmptyDeck(){
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        for (int i = 0; i < 34; i++) {
            assertDoesNotThrow( () -> gameAssociatedController.CONTROLLED_GAME.getResourceCardsDeck().draw());
        }
        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromDeck(currentPlayerInTurn, "resource");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(EmptyDeckException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnknownStringVisibleCard() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromVisibleCards(currentPlayerInTurn, "Deck", 1);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnknownStringException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToInvalidDeckPositionVisibleCard() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromVisibleCards(currentPlayerInTurn, "resource", 3);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(InvalidDeckPositionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnexpectedPlayerVisibleCard() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        NetworkSession currentPlayerNotInTurn;

        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerNotInTurn = inGamePlayer_2;
        else
            currentPlayerNotInTurn = inGamePlayer_1;

        gameAssociatedController.drawFromVisibleCards(currentPlayerNotInTurn, "resource", 2);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToEmptyDeckVisibleCard(){
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        for (int i = 0; i < 34; i++) {
            assertDoesNotThrow( () -> gameAssociatedController.CONTROLLED_GAME.getResourceCardsDeck().draw());
        }
        assertDoesNotThrow( () -> gameAssociatedController.CONTROLLED_GAME.drawFrom(gameAssociatedController.CONTROLLED_GAME.getPlacedResources(), 1));

        gameAssociatedController.drawFromVisibleCards(currentPlayerInTurn, "resource", 1);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(EmptyDeckException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToNotExistingPlayerInDirectMessage() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.directMessage(currentPlayerInTurn, "PlayerNotCreated", "Test");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(NotExistingPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctDirectMessageReceived() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        NetworkSession otherPlayer;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer")) {
            currentPlayerInTurn = inGamePlayer_1;
            otherPlayer = inGamePlayer_2;
            gameAssociatedController.directMessage(currentPlayerInTurn, "thePlayer_2", "Test");
        } else {
            currentPlayerInTurn = inGamePlayer_2;
            otherPlayer = inGamePlayer_1;
            gameAssociatedController.directMessage(currentPlayerInTurn, "thePlayer", "Test");
        }
        assertEquals("Test", ((ServerControllerTest.VirtualClientImpl) ((ServerListener) otherPlayer.getListener()).getVirtualClient()).myClientController.receivedMessage);
    }

    @Test
    void correctBroadcastMessageReceived() {
        doPickObjectivesSelection();

        NetworkSession currentPlayerInTurn;
        NetworkSession otherPlayer;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer")) {
            currentPlayerInTurn = inGamePlayer_1;
            otherPlayer = inGamePlayer_2;
        } else {
            currentPlayerInTurn = inGamePlayer_2;
            otherPlayer = inGamePlayer_1;
        }

        gameAssociatedController.broadcastMessage(currentPlayerInTurn, "Test");


        assertEquals("Test", ((ServerControllerTest.VirtualClientImpl) ((ServerListener) otherPlayer.getListener()).getVirtualClient()).myClientController.receivedMessage);
    }

    @Test
    void correctRestoreGame() {
        doPickObjectivesSelection();

        NetworkSession restoreGamePlayer = inGamePlayer_1;
        gameAssociatedController.leaveGame(inGamePlayer_1);

        gameAssociatedController.generatePlayer(restoreGamePlayer, "thePlayer");

        assertInstanceOf(RestoreGameCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
    }

    @Test
    void runTimeOutTimerTaskWhileInAGame(){
        connectionController.createTimeoutTask(inGamePlayer_1).run();
    }

    @Test
    void givingAnInvalidCardID(){
        doPickObjectivesSelection();
        gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(1,1), 1000, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(IllegalArgumentException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void tryingToPlaceAnObjectiveCard(){
        doPickObjectivesSelection();
        gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(1,1), 100, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(InvalidCardTypeException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void tryingToPlaceCardsInWrongStates(){
        doPickObjectivesSelection();
        gameAssociatedController.getCurrentState().transition();

        gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(1,1), 2, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(ForbiddenActionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void tryingToDrawCardsInWrongStates(){
        doPickObjectivesSelection();
        gameAssociatedController.drawFromDeck(inGamePlayer_1, "resource");

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(ForbiddenActionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);

        gameAssociatedController.drawFromVisibleCards(inGamePlayer_1, "resource", 0);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(ForbiddenActionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);

    }

    @Test
    void tryingToPickObjectiveWithWrongID(){
        gameAssociatedController.pickObjective(inGamePlayer_1, 1000);

        assertNull( ((InGamePlayer) inGamePlayer_1.getPlayer()).getSecretObjective());
    }

    @Test
    void tryingToPickObjectiveWrongStates(){
        doPickObjectivesSelection();
        gameAssociatedController.pickObjective(inGamePlayer_1, 100);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(ForbiddenActionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void tryingToPickNotAnObjectiveCard(){
        gameAssociatedController.pickObjective(inGamePlayer_1, 1);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(InvalidCardTypeException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void tryingToPickAnObjectiveCardNotInMyHand(){
        gameAssociatedController.pickObjective(inGamePlayer_1,
                ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(CardNotInHandException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void tryingToPickAnObjectiveButItWasAlreadyDoneBefore(){
        gameAssociatedController.pickObjective(inGamePlayer_1,
                ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());

        gameAssociatedController.pickObjective(inGamePlayer_1,
                ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getLast());

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(AlreadySetCardException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void sendDirectMessageToANotExistingPlayer(){
        doPickObjectivesSelection();
        gameAssociatedController.directMessage(inGamePlayer_1, "missing", "Test Message");

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(NotExistingPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void sendDirectMessageToAnInactivePlayerPlayer(){
        doPickObjectivesSelection();
        gameAssociatedController.CONTROLLED_GAME.toggleActive((InGamePlayer) inGamePlayer_2.getPlayer());
        gameAssociatedController.directMessage(inGamePlayer_1, inGamePlayer_2.getPlayer().getNickname(), "Test Message");

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).lastCommandReceived);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedException);
    }

}