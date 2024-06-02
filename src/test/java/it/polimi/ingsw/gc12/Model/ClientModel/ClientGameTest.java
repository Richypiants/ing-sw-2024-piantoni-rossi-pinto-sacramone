package it.polimi.ingsw.gc12.Model.ClientModel;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.*;
import it.polimi.ingsw.gc12.Network.NetworkSession;
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
    ServerModel model;
    Lobby lobby;
    Game game;
    ClientGame clientGame;
    GameController gameController;
    NetworkSession client1;
    NetworkSession client2;
    ChooseObjectiveCardsState state;


    @BeforeEach
    void setGameParameters() throws Exception {

        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {});
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {});
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {});
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {});

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
            protected Listener createListener(NetworkSession session) {
                return new Listener(session, command -> {
                });
            }
        };
        client2 = new NetworkSession(gameController) {
            @Override
            protected Listener createListener(NetworkSession session) {
                return new Listener(session, command -> {
                });
            }
        };

        ServerController.activePlayers.put(client1, game.getPlayers().get(0));
        ServerController.activePlayers.put(client2, game.getPlayers().get(1));
        gameController.getCurrentState().transition();

        int i = 0;
        for (var target : game.getPlayers()) {
            target.placeCard(new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            target.addCardToHand(resourceCards.get(i++));
            target.addCardToHand(resourceCards.get(i));
            target.addCardToHand(goldCards.get(i++));
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
    void getterTest() {
        clientGame = game.generateDTO(game.getCurrentPlayer());
        assertInstanceOf(ClientPlayer.class, clientGame.getThisPlayer());

        assertInstanceOf(ArrayList.class, clientGame.getCardsInHand());
        assert (!clientGame.getCardsInHand().isEmpty());

        assertInstanceOf(ClientCard.class, clientGame.getOwnObjective());
        assert (!clientGame.getOwnObjective().GUI_SPRITES.isEmpty());
        assert (!clientGame.getOwnObjective().TUI_SPRITES.isEmpty());

        assertInstanceOf(ClientCard[].class, clientGame.getPlacedGolds());
        assert (!Arrays.stream(clientGame.getPlacedGolds()).toList().isEmpty());

        assertInstanceOf(ClientCard[].class, clientGame.getPlacedResources());
        assert (!Arrays.stream(clientGame.getPlacedResources()).toList().isEmpty());

        assertInstanceOf(ClientCard[].class, clientGame.getCommonObjectives());
        assert (!Arrays.stream(clientGame.getCommonObjectives()).toList().isEmpty());

        clientGame.setCurrentPlayerIndex(1);
        assertEquals(1, clientGame.getCurrentPlayerIndex());

        clientGame.addMessageToChatLog("ciao");
        assert (!clientGame.getChatLog().isEmpty());

        assertInstanceOf(ClientCard.class, clientGame.getTopDeckGoldCard());
        assert (!clientGame.getTopDeckGoldCard().GUI_SPRITES.isEmpty());
        assert (!clientGame.getTopDeckGoldCard().TUI_SPRITES.isEmpty());

        assertInstanceOf(ClientCard.class, clientGame.getTopDeckResourceCard());
        assert (!clientGame.getTopDeckResourceCard().GUI_SPRITES.isEmpty());
        assert (!clientGame.getTopDeckResourceCard().TUI_SPRITES.isEmpty());

        int roundNumberTest = 10;
        clientGame.setCurrentRound(roundNumberTest);
        assertEquals(roundNumberTest, clientGame.getCurrentRound());

        assertEquals(2, clientGame.getMaxPlayers());
        assertEquals(2, clientGame.getPlayersNumber());
    }

    @Test
    void setterTest() {
        clientGame = game.generateDTO(game.getCurrentPlayer());
        ClientCard card = new ClientCard(1, new HashMap<>(), new HashMap<>());
        clientGame.setCurrentRound(1);
        assertEquals(1, clientGame.getCurrentRound());

        clientGame.addCardToHand(new ClientCard(1, new HashMap<>(), new HashMap<>()));
        assert (!clientGame.getCardsInHand().isEmpty());
        clientGame.removeCardFromHand(new ClientCard(1, new HashMap<>(), new HashMap<>()));
        assert (!clientGame.getCardsInHand().contains(new ClientCard(1, new HashMap<>(), new HashMap<>())));

        clientGame.setPlacedResources(card, 1);
        assertEquals(card, clientGame.getPlacedResources()[1]);

        clientGame.setPlacedGold(card, 1);
        assertEquals(card, clientGame.getPlacedGolds()[1]);

        clientGame.setCommonObjectives(card, 1);
        assertEquals(card, clientGame.getCommonObjectives()[1]);

        clientGame.setTopDeckGoldCard(card);
        assertEquals(card, clientGame.getTopDeckGoldCard());

        clientGame.setTopDeckResourceCard(card);
        assertEquals(card, clientGame.getTopDeckResourceCard());

        clientGame.setOwnObjective(card);
        assertEquals(card, clientGame.getOwnObjective());
    }
}
