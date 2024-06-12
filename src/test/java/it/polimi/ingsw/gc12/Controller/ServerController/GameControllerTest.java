package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.GameState;
import it.polimi.ingsw.gc12.Listeners.ServerListener;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GameControllerTest {
    static NetworkSession inGamePlayer_1;
    static NetworkSession inGamePlayer_2;
    static ConnectionController connectionController = ConnectionController.getInstance();;

    static GameController gameAssociatedController;

    @BeforeEach
    void initializingSessions() {
        inGamePlayer_1 = createNetworkSessionStub(connectionController);
        inGamePlayer_2 = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(inGamePlayer_1, "thePlayer");
        connectionController.generatePlayer(inGamePlayer_2, "thePlayer_2");

        connectionController.createLobby(inGamePlayer_1, 2);
        connectionController.joinLobby(inGamePlayer_2, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) inGamePlayer_1.getController();
        associatedLobbyController.pickColor(inGamePlayer_1, Color.RED);
        associatedLobbyController.pickColor(inGamePlayer_2, Color.GREEN);

        gameAssociatedController = (GameController) inGamePlayer_1.getController();
        gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(0, 0), ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.lastReceivedCardIDs.getFirst(), Side.FRONT);
        gameAssociatedController.placeCard(inGamePlayer_2, new GenericPair<>(0, 0), ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.lastReceivedCardIDs.getFirst(), Side.FRONT);

        gameAssociatedController.pickObjective(inGamePlayer_1, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());
        gameAssociatedController.pickObjective(inGamePlayer_2, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_2.getListener()).getVirtualClient()).myClientController.receivedObjectiveIDs.getLast());

    }

    @Test
    void correctCallToCardNotInHand() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), 84, Side.BACK);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(CardNotInHandException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToNotEnoughResources() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().get(2).ID, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(NotEnoughResourcesException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToInvalidPosition() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(3, 3), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(InvalidCardPositionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnexpectedPlayerPlay() {
        NetworkSession currentPlayerNotInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerNotInTurn = inGamePlayer_2;
        else
            currentPlayerNotInTurn = inGamePlayer_1;

        gameAssociatedController.placeCard(currentPlayerNotInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnexpectedPlayerDrawDeck() {
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


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnknownStringDeck() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromDeck(currentPlayerInTurn, "Deck");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(UnknownStringException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToEmptyDeck() throws Exception {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        for (int i = 0; i < 34; i++) {
            gameAssociatedController.CONTROLLED_GAME.getResourceCardsDeck().draw();
        }
        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromDeck(currentPlayerInTurn, "resource");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(EmptyDeckException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnknownStringVisibleCard() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromVisibleCards(currentPlayerInTurn, "Deck", 1);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(UnknownStringException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToInvalidDeckPositionVisibleCard() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        gameAssociatedController.drawFromVisibleCards(currentPlayerInTurn, "resource", 3);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(InvalidDeckPositionException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToUnexpectedPlayerVisibleCard() {
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


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(UnexpectedPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerNotInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToEmptyDeckVisibleCard() throws Exception {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(1, 1), gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getCardsInHand().getFirst().ID, Side.FRONT);

        for (int i = 0; i < 34; i++) {
            gameAssociatedController.CONTROLLED_GAME.getResourceCardsDeck().draw();
        }
        gameAssociatedController.CONTROLLED_GAME.drawFrom(gameAssociatedController.CONTROLLED_GAME.getPlacedResources(), 1);

        gameAssociatedController.drawFromVisibleCards(currentPlayerInTurn, "resource", 1);


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(EmptyDeckException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctCallToNotExistingPlayerInDirectMessage() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.directMessage(currentPlayerInTurn, "PlayerNotCreated", "Test");


        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).receivedCommand);
        assertInstanceOf(NotExistingPlayerException.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) currentPlayerInTurn.getListener()).getVirtualClient()).myClientController.receivedException);
    }

    @Test
    void correctDirectMessageReceived() {
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


    //TODO: Fix the fact that Server console.readLine() is null since we are in testing for both the following methods
    /*
    @Test
    void correctLeaveLobbyRoutine() {
        assertEquals(2, gameAssociatedController.CONTROLLED_GAME.getActivePlayers().size());
        gameAssociatedController.leaveGame(inGamePlayer_1);
        assertEquals(1, gameAssociatedController.CONTROLLED_GAME.getActivePlayers().size());
    }

    @Test
    void correctRestoreGame() {
        NetworkSession restoreGamePlayer = inGamePlayer_1;
        gameAssociatedController.leaveGame(inGamePlayer_1);

        assertEquals(1, gameAssociatedController.CONTROLLED_GAME.getActivePlayers().size());

        gameAssociatedController.generatePlayer(restoreGamePlayer, "thePlayer");

        assertEquals(2, gameAssociatedController.CONTROLLED_GAME.getActivePlayers().size());
        assertInstanceOf(RestoreGameCommand.class, ((ServerControllerTest.VirtualClientImpl) ((ServerListener) inGamePlayer_1.getListener()).getVirtualClient()).receivedCommand);
    }*/

    static class GameStatesDriver extends GameState {
        public String thrownException;

        public GameStatesDriver(GameController controller, Game thisGame, String state) {
            super(controller, thisGame, state);
        }

        public synchronized void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card,
                                           Side playedSide)
                throws UnexpectedPlayerException, CardNotInHandException, NotEnoughResourcesException,
                InvalidCardPositionException {
            switch (thrownException) {
                case "CardNotInHandException" -> throw new CardNotInHandException();
                case "NotEnoughResourcesException" -> throw new NotEnoughResourcesException();
                case "InvalidCardPositionException" -> throw new InvalidCardPositionException();
                case "UnexpectedPlayerException" -> throw new UnexpectedPlayerException();
            }
        }

        public synchronized void drawFrom(InGamePlayer target, String deck) throws UnexpectedPlayerException,
                UnknownStringException, EmptyDeckException {
            switch (thrownException) {
                case "EmptyDeckException" -> throw new EmptyDeckException();
                case " UnknownStringException" -> throw new UnknownStringException();
                case "UnexpectedPlayerException" -> throw new UnexpectedPlayerException();
            }

        }

        public synchronized void drawFrom(InGamePlayer target, String whichType, int position)
                throws UnexpectedPlayerException, InvalidDeckPositionException, UnknownStringException, EmptyDeckException {
            switch (thrownException) {
                case "EmptyDeckException" -> throw new EmptyDeckException();
                case " UnknownStringException" -> throw new UnknownStringException();
                case "UnexpectedPlayerException" -> throw new UnexpectedPlayerException();
                case "InvalidDeckPositionException" -> throw new InvalidDeckPositionException();
            }

        }

        public void forceException(String e) {
            this.thrownException = e;
        }

        @Override
        public void playerDisconnected(InGamePlayer target) {
        }

        @Override
        public void transition() {

        }


    }







}