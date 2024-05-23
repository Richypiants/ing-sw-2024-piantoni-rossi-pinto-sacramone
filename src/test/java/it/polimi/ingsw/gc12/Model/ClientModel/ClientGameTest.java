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
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ClientGameTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player1;
    Player player2;
    GameLobby lobby;
    Game game;
    ClientGame client;
    VirtualClient client1;
    VirtualClient client2;
    ChooseObjectiveCardsState state;


    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("Sacri");
        player2 = new Player("Piants");
        lobby = new GameLobby(player1, 2);
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
        ServerController.lobbiesAndGames.put(lobbyUUID, game);
        ServerController.players.put(client1, game.getPlayers().getFirst());
        ServerController.players.put(client2, game.getPlayers().getLast());
        GameController gameController = new GameController(game);
        ServerController.playersToControllers.put(game.getPlayers().getFirst(), gameController);
        ServerController.playersToControllers.put(game.getPlayers().getLast(), gameController);
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
        }

        game.getCurrentState().placeCard(game.getPlayers().getFirst(), new GenericPair<>(1, 1), game.getPlayers().getFirst().getCardsInHand().getFirst(), Side.FRONT);

    }

    @Test
    void getterTest() {
        client = new ClientGame(game, game.getPlayers().getFirst());
        assertInstanceOf(ClientPlayer.class, client.getThisPlayer());

        assertInstanceOf(ArrayList.class, client.getCardsInHand());
        assert (!client.getCardsInHand().isEmpty());

        assertInstanceOf(ClientCard.class, client.getOwnObjective());
        assert (!client.getOwnObjective().GUI_SPRITES.isEmpty());
        assert (!client.getOwnObjective().TUI_SPRITES.isEmpty());

        assertInstanceOf(ClientCard[].class, client.getPlacedGolds());
        assert (!Arrays.stream(client.getPlacedGolds()).toList().isEmpty());

        assertInstanceOf(ClientCard[].class, client.getPlacedResources());
        assert (!Arrays.stream(client.getPlacedResources()).toList().isEmpty());

        assertInstanceOf(ClientCard[].class, client.getCommonObjectives());
        assert (!Arrays.stream(client.getCommonObjectives()).toList().isEmpty());

        client.setCurrentPlayerIndex(1);
        assertEquals(1, client.getCurrentPlayerIndex());

        client.addMessageToChatLog("ciao");
        assert (!client.getChatLog().isEmpty());

        assertInstanceOf(ClientCard.class, client.getTopDeckGoldCard());
        assert (!client.getTopDeckGoldCard().GUI_SPRITES.isEmpty());
        assert (!client.getTopDeckGoldCard().TUI_SPRITES.isEmpty());

        assertInstanceOf(ClientCard.class, client.getTopDeckResourceCard());
        assert (!client.getTopDeckResourceCard().GUI_SPRITES.isEmpty());
        assert (!client.getTopDeckResourceCard().TUI_SPRITES.isEmpty());

        int roundNumberTest = 10;
        client.setCurrentRound(roundNumberTest);
        assertEquals(roundNumberTest, client.getCurrentRound());

        assertEquals(2, client.getMaxPlayers());
        assertEquals(1, client.getPlayersNumber());
    }

    @Test
    void setterTest() {
        client = new ClientGame(game, game.getPlayers().getFirst());

        client.setCurrentRound(1);
        assertEquals(1, client.getCurrentRound());


    }
}
