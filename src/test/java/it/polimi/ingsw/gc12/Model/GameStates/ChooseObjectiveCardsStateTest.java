package it.polimi.ingsw.gc12.Model.GameStates;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
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
    GameLobby lobby;
    Game game;
    VirtualClient client1;
    VirtualClient client2;
    ServerController server;
    private static ArrayList<ObjectiveCard> objectiveCards;

    @BeforeEach
    void setGameParameters() throws Exception {
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });
        player1 = new Player("giovanni");
        player2 = new Player("paolo");
        lobby = new GameLobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);

        server = server.getInstance();
        client1 = new VirtualClient() {
            @Override
            public void requestToClient(ClientCommand command) throws Exception {

            }
        };

        client2 = new VirtualClient() {
            @Override
            public void requestToClient(ClientCommand command) throws Exception {

            }
        };

        UUID lobbyUUID = UUID.randomUUID();
        server.lobbiesAndGames.put(lobbyUUID, game);
        server.players.put(client1, game.getPlayers().get(0));
        server.players.put(client2, game.getPlayers().get(1));
        server.playersToLobbiesAndGames.put(game.getPlayers().get(0), game);
        server.playersToLobbiesAndGames.put(game.getPlayers().get(1), game);


    }

    @Test
    void correctTransitionTest() throws Exception {
        SetupState state = new SetupState(game);
        state.transition();
        for (var target : game.getPlayers()) {
            game.getCurrentState().placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
        }
        game.getCurrentState().transition();
        assertInstanceOf(PlayerTurnPlayState.class, game.getCurrentState());
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

        state = new ChooseObjectiveCardsState(game, objectivesMap);

        for (var target : game.getPlayers()) {
            state.pickObjective(target, objectivesMap.get(target).getFirst());
            if (target != game.getPlayers().getLast()) {
                assertInstanceOf(SetupState.class, game.getCurrentState());
            } else {
                assertInstanceOf(PlayerTurnPlayState.class, game.getCurrentState());
            }
        }


    }

}