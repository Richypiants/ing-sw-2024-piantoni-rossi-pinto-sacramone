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
import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.virtualClient;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GameControllerTest {
    static NetworkSession inGamePlayer_1;
    static NetworkSession inGamePlayer_2;
    static ConnectionController connectionController = ConnectionController.getInstance();
    static ServerControllerTest.ClientControllerInterfaceImpl clientController = ServerControllerTest.clientController;
    static ServerControllerTest.ClientControllerInterfaceImpl clientController_2 = ServerControllerTest.clientController;

    static GameController gameAssociatedController;

    @BeforeAll
    static void initializingSessions() {
        inGamePlayer_1 = createNetworkSessionStub(connectionController, virtualClient);
        inGamePlayer_2 = createNetworkSessionStub(connectionController, virtualClient);

        connectionController.generatePlayer(inGamePlayer_1, "thePlayer");
        connectionController.generatePlayer(inGamePlayer_2, "thePlayer_2");

        connectionController.createLobby(inGamePlayer_1, 2);
        connectionController.joinLobby(inGamePlayer_2, clientController.receivedUUID);

        LobbyController associatedLobbyController = (LobbyController) inGamePlayer_1.getController();
        associatedLobbyController.pickColor(inGamePlayer_1, Color.RED);
        associatedLobbyController.pickColor(inGamePlayer_2, Color.GREEN);

        gameAssociatedController = (GameController) inGamePlayer_1.getController();

        if (gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getNickname().equals("thePlayer")) {
            gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(0, 0), gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getCardsInHand().getFirst().ID, Side.FRONT);
        } else {
            gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(0, 0), gameAssociatedController.CONTROLLED_GAME.getPlayers().getLast().getCardsInHand().getFirst().ID, Side.FRONT);
        }

        if (gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getNickname().equals("thePlayer2")) {
            gameAssociatedController.placeCard(inGamePlayer_2, new GenericPair<>(0, 0), gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getCardsInHand().getFirst().ID, Side.FRONT);
        } else {
            gameAssociatedController.placeCard(inGamePlayer_2, new GenericPair<>(0, 0), gameAssociatedController.CONTROLLED_GAME.getPlayers().getLast().getCardsInHand().getFirst().ID, Side.FRONT);
        }

        gameAssociatedController.pickObjective(inGamePlayer_1, clientController.recivedObjectiveIDs.getFirst());
        gameAssociatedController.pickObjective(inGamePlayer_2, clientController.recivedObjectiveIDs.getLast());

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
        if (gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getNickname().equals("thePlayer")) {
            gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(2, 2), gameAssociatedController.CONTROLLED_GAME.getPlayers().getFirst().getCardsInHand().getFirst().ID, Side.FRONT);
        } else {
            gameAssociatedController.placeCard(inGamePlayer_1, new GenericPair<>(2, 2), gameAssociatedController.CONTROLLED_GAME.getPlayers().getLast().getCardsInHand().getFirst().ID, Side.FRONT);
        }

        assertInstanceOf(ThrowExceptionCommand.class, virtualClient.receivedCommand);
        assertInstanceOf(InvalidCardPositionException.class, clientController.receivedException);


    }

}