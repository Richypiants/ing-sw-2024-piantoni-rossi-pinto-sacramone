package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.ServerController.ServerControllerTest.createNetworkSessionStub;

class PlayerTurnDrawStateTest {

    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    ServerController server;
    GameController gameController;
    ChooseObjectiveCardsState state;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        });
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });
    }

    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);

        UUID lobbyUUID = UUID.randomUUID();

        gameController = new GameController(game);
        ServerController.model.GAME_CONTROLLERS.put(lobbyUUID, gameController);

        client1 = createNetworkSessionStub(gameController);
        client2 = createNetworkSessionStub(gameController);

        ServerController.activePlayers.put(client1, game.getPlayers().get(0));
        ServerController.activePlayers.put(client2, game.getPlayers().get(1));
        gameController.getCurrentState().transition();

        int i = 0;
        for (var target : game.getPlayers()) {
            game.placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            target.addCardToHand(resourceCards.get(i));
            i++;
            target.addCardToHand(resourceCards.get(i));
            target.addCardToHand(goldCards.get(i));
            i++;
        }

        gameController.pickObjective(client1, ((ServerControllerTest.VirtualClientImpl) client1.getListener().getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());
        gameController.pickObjective(client2, ((ServerControllerTest.VirtualClientImpl) client2.getListener().getVirtualClient()).myClientController.receivedObjectiveIDs.getFirst());

        gameController.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(1, 1), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);
    }
/*
    @Test
    void correctTransitionTest_Draw1() throws Exception {
        gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "Resource");
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    @Test
    void correctTransitionTest_Draw2() throws Exception {
        gameController.getCurrentState().drawFrom(game.getCurrentPlayer(), "Resource", 1);
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
        assertEquals(game.getPlayers().getLast(), game.getCurrentPlayer());
    }

    @Test
    void correctUnexpectedPlayerExceptionCall() throws Exception {
        assertThrows(UnexpectedPlayerException.class, () -> gameController.getCurrentState().drawFrom(game.getPlayers().getLast(), "Resource"));

    }

    @Test
    void correctInvalidDeckPositionException() throws Exception {
        assertThrows(InvalidDeckPositionException.class,
                () -> gameController.getCurrentState().drawFrom(
                        game.getCurrentPlayer(), "Resource", 3
                )
        );

    }*/




}