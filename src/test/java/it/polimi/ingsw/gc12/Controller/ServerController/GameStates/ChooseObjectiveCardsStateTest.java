package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ChooseObjectiveCardsStateTest {

    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    NetworkSession client1;
    NetworkSession client2;
    ServerController server;
    GameController gameController;
    private static ArrayList<ObjectiveCard> objectiveCards;

    @BeforeEach
    void setGameParameters() throws Exception {
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);

        UUID lobbyUUID = UUID.randomUUID();

        gameController = new GameController(game);
        ServerController.model.GAME_CONTROLLERS.put(lobbyUUID, gameController);

        client1 = new NetworkSession(gameController) {
            @Override
            protected Listener createListener() {
                return new Listener(command -> {
                });
            }
        };
        client2 = new NetworkSession(gameController) {
            @Override
            protected Listener createListener() {
                return new Listener(command -> {
                });
            }
        };

        ServerController.activePlayers.put(client1, game.getPlayers().get(0));
        ServerController.activePlayers.put(client2, game.getPlayers().get(1));
    }

    @Test
    void correctTransitionTest() throws Exception {
        SetupState state = new SetupState(gameController, game);
        state.transition();
        for (var target : game.getPlayers()) {
            gameController.getCurrentState().placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
        }
        gameController.getCurrentState().transition();
        assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
    }


    @Test
    void transitionStartAfterAllcheck() throws Exception {
        ChooseObjectiveCardsState state;
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
            if (target != game.getPlayers().getLast()) {
                assertInstanceOf(SetupState.class, gameController.getCurrentState());
            } else {
                assertInstanceOf(PlayerTurnPlayState.class, gameController.getCurrentState());
            }
        }


    }

}