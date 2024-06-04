package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.GameState;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Network.NetworkSession;
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

class GameControllerTest {
    static NetworkSession inGamePlayer_1;
    static NetworkSession inGamePlayer_2;
    static ConnectionController connectionController = ConnectionController.getInstance();

    LobbyController lobbyController = new LobbyController(null);
    GameController gameController = new GameController(null);

    @BeforeAll
    static void initializingSessions() {
        inGamePlayer_1 = createNetworkSessionStub(connectionController, virtualClient);
        inGamePlayer_2 = createNetworkSessionStub(connectionController, virtualClient);

        connectionController.generatePlayer(inGamePlayer_1, "thePlayer");
        connectionController.generatePlayer(inGamePlayer_2, "thePlayer_2");

    }

    @Test
    void InvalidCardCall() {

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

}