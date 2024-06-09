package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.GameState;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GameControllerTest {
    static NetworkSession inGamePlayer_1;
    static NetworkSession inGamePlayer_2;
    static ConnectionController connectionController = ConnectionController.getInstance();;

    static GameController gameAssociatedController;

    @BeforeAll
    static void initializingSessions() {
        inGamePlayer_1 = createNetworkSessionStub(connectionController);
        inGamePlayer_2 = createNetworkSessionStub(connectionController);

        connectionController.generatePlayer(inGamePlayer_1, "thePlayer");
        connectionController.generatePlayer(inGamePlayer_2, "thePlayer_2");

        connectionController.createLobby(inGamePlayer_1, 2);
        connectionController.joinLobby(inGamePlayer_2, ((ServerControllerTest.VirtualClientImpl) inGamePlayer_2.getListener().getVirtualClient()).myClientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) inGamePlayer_1.getController();
        associatedLobbyController.pickColor(inGamePlayer_1, Color.RED);
        associatedLobbyController.pickColor(inGamePlayer_2, Color.GREEN);

        gameAssociatedController = (GameController) inGamePlayer_1.getController();
        gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(0, 0), ((ServerControllerTest.VirtualClientImpl) inGamePlayer_1.getListener().getVirtualClient()).myClientController.lastReceivedCardIDs.getFirst(), Side.FRONT);
        gameAssociatedController.placeCard(inGamePlayer_2, new GenericPair<>(0, 0), ((ServerControllerTest.VirtualClientImpl) inGamePlayer_2.getListener().getVirtualClient()).myClientController.lastReceivedCardIDs.getFirst(), Side.FRONT);

        gameAssociatedController.pickObjective(inGamePlayer_1, ((ServerControllerTest.VirtualClientImpl) inGamePlayer_1.getListener().getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());
        gameAssociatedController.pickObjective(inGamePlayer_2, ((ServerControllerTest.VirtualClientImpl) inGamePlayer_2.getListener().getVirtualClient()).myClientController.receivedObjectiveIDs.getLast());

    }

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

        public void forceException(String e) {
            this.thrownException = e;
        }


        @Override
        public void transition() {

        }


    }

    @Test
    void correctCallToCardNotInHand() {
        NetworkSession currentPlayerInTurn;
        if (gameAssociatedController.CONTROLLED_GAME.getCurrentPlayer().getNickname().equals("thePlayer"))
            currentPlayerInTurn = inGamePlayer_1;
        else
            currentPlayerInTurn = inGamePlayer_2;

        gameAssociatedController.placeCard(currentPlayerInTurn, new GenericPair<>(2, 2), gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getCardsInHand().getFirst().ID, Side.BACK);

        assertInstanceOf(ThrowExceptionCommand.class, ((ServerControllerTest.VirtualClientImpl) currentPlayerInTurn.getListener().getVirtualClient()).receivedCommand);
        assertInstanceOf(InvalidCardPositionException.class, ((ServerControllerTest.VirtualClientImpl) currentPlayerInTurn.getListener().getVirtualClient()).myClientController.receivedException);
    }

}