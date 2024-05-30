package it.polimi.ingsw.gc12.Model.ClientModel;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ClientPlayerTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    ClientGame client;
    VirtualClient client1;
    VirtualClient client2;
    ChooseObjectiveCardsState state;


    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("Sacri");
        player2 = new Player("Piants");
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        });
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });

        client1 = command -> {
        };
        client2 = command -> {
        };

        UUID lobbyUUID = UUID.randomUUID();
        ServerController.model.ROOMS.put(lobbyUUID, game);
        ServerController.players.put(client1, game.getPlayers().getFirst());
        ServerController.players.put(client2, game.getPlayers().getLast());
        GameController gameController = new GameController(game);
        ServerController.playersToControllers.put(game.getPlayers().getFirst(), gameController);
        ServerController.playersToControllers.put(game.getPlayers().getLast(), gameController);
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

    @Test
    void getterESetterTest() {
        ClientPlayer player = new ClientPlayer(player1, null, null, 0);

        assertEquals("Sacri", player.getNickname());

        player.placeCard(new GenericPair<>(1, 1), new ClientCard(1, new HashMap<>(), new HashMap<>()), Side.FRONT);
        assertInstanceOf(LinkedHashMap.class, player.getPlacedCards());
        assert (!player.getPlacedCards().isEmpty());

        EnumMap<Resource, Integer> map = new EnumMap<>(Resource.class);
        map.put(Resource.INSECT, 1);
        player.setOwnedResources(map);
        assertInstanceOf(EnumMap.class, player.getOwnedResources());
        assert (!player.getOwnedResources().values().isEmpty());

        ArrayList<GenericPair<Integer, Integer>> list = new ArrayList<>();
        list.add(new GenericPair<>(0, 1));
        list.add(new GenericPair<>(0, -1));
        list.add(new GenericPair<>(1, 0));
        list.add(new GenericPair<>(1, 0));

        player.setOpenCorners(list);
        assertInstanceOf(List.class, player.getOpenCorners());
        assert (!player.getOpenCorners().isEmpty());

        player.setPoints(1);
        assertEquals(1, player.getPoints());

        player.setColor(Color.RED);
        assertEquals(Color.RED, player.getColor());

        assert (player.isActive());
        player.toggleActive();
        assert (!player.isActive());


    }
}