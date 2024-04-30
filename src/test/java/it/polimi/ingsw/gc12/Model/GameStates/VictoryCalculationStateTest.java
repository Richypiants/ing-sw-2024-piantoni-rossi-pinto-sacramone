package it.polimi.ingsw.gc12.Model.GameStates;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class VictoryCalculationStateTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player1;
    Player player2;
    GameLobby lobby;
    Game game;
    VirtualClient client1;
    VirtualClient client2;
    ServerController server;
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
        lobby = new GameLobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);

        ObjectiveCard[] objectiveCards1 = new ObjectiveCard[]{objectiveCards.get(4), objectiveCards.get(5)};
        game.setCommonObjectives(objectiveCards1);

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
        game.getCurrentState().transition();

        int i = 0;
        for (var target : game.getPlayers()) {
            target.placeCard(new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            target.addCardToHand(resourceCards.get(i));
            i++;
            target.addCardToHand(resourceCards.get(i));
            target.addCardToHand(goldCards.get(i));
            i++;
        }

        ObjectiveCard[] objectiveCards2 = new ObjectiveCard[]{objectiveCards.get(4), objectiveCards.get(2)};
        game.setCommonObjectives(objectiveCards2);

        Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesMap = new HashMap<>();
        ArrayList<ObjectiveCard> obj_a = new ArrayList<>();
        obj_a.add(objectiveCards.getFirst());
        obj_a.add(objectiveCards.get(1));

        ArrayList<ObjectiveCard> obj_a2 = new ArrayList<>();
        obj_a2.add(objectiveCards.get(6));
        obj_a2.add(objectiveCards.get(3));

        objectivesMap.put(game.getPlayers().getFirst(), obj_a);
        objectivesMap.put(game.getPlayers().getLast(), obj_a2);

        state = new ChooseObjectiveCardsState(game, objectivesMap);

        for (var target : game.getPlayers()) {
            state.pickObjective(target, objectivesMap.get(target).getFirst());
        }

    }

    // TODO: tested with debugger (All run correctly ) , find a way to return the EndGame Command
    @Test
    void correctVictoryTest() throws Exception {
        game.getPlayers().getFirst().increasePoints(20);
        PlayerTurnPlayState state1 = new PlayerTurnPlayState(game, state.currentPlayer, 0);
        game.getCurrentState().transition();
        for (int i = 0; i < 7; i++) {

            game.getCurrentState().transition();


        }
        game.getCurrentState().transition();

        assertInstanceOf(VictoryCalculationState.class, game.getCurrentState());

    }

    @Test
    void correctVictoryTestOnEqualPoints() throws Exception {
        game.getPlayers().getFirst().increasePoints(20);
        game.getPlayers().getLast().increasePoints(20);
        game.getPlayers().getFirst().addCardToHand(resourceCards.getFirst());
        game.getPlayers().getFirst().addCardToHand(resourceCards.get(3));
        game.getPlayers().getFirst().addCardToHand(resourceCards.get(5));
        game.getPlayers().getLast().addCardToHand(resourceCards.get(20));
        game.getPlayers().getLast().addCardToHand(resourceCards.get(21));
        game.getPlayers().getLast().addCardToHand(resourceCards.get(24));
        game.getPlayers().getFirst().placeCard(new GenericPair<>(1, 1), resourceCards.getFirst(), Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(2, 2), resourceCards.get(3), Side.FRONT);
        game.getPlayers().getFirst().placeCard(new GenericPair<>(3, 3), resourceCards.get(5), Side.FRONT);
        game.getPlayers().getLast().placeCard(new GenericPair<>(1, 1), resourceCards.get(20), Side.FRONT);
        game.getPlayers().getLast().placeCard(new GenericPair<>(2, 2), resourceCards.get(21), Side.FRONT);
        game.getPlayers().getLast().placeCard(new GenericPair<>(3, 3), resourceCards.get(24), Side.FRONT);
        PlayerTurnPlayState state1 = new PlayerTurnPlayState(game, state.currentPlayer, 0);
        game.getCurrentState().transition();
        for (int i = 0; i < 7; i++) {

            game.getCurrentState().transition();


        }
        game.getCurrentState().transition();

        assertInstanceOf(VictoryCalculationState.class, game.getCurrentState());

    }
}