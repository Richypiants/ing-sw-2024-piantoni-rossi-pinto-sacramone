package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.*;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ServerControllerTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;
    private static ArrayList<ObjectiveCard> objectiveCards;
    Player player1;
    Player player2;
    Lobby lobby;
    Game game;
    ClientGame client;
    NetworkSession client1;
    NetworkSession client2;
    ChooseObjectiveCardsState state;

    ConnectionController controller = ConnectionController.getInstance();
    GameController gameController;
    ClientControllerInterface Interface = new ClientControllerInterface() {
        @Override
        public void throwException(Exception e) {
            assertInstanceOf(ForbiddenActionException.class, e);
        }

        @Override
        public void keepAlive() {

        }

        @Override
        public void setNickname(String nickname) {

        }

        @Override
        public void restoreGame(UUID gameUUID, ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD) {

        }

        @Override
        public void setLobbies(Map<UUID, Room> lobbies) {

        }

        @Override
        public void updateLobby(UUID lobbyUUID, Lobby lobby) {

        }

        @Override
        public void startGame(UUID lobbyUUID, ClientGame gameDTO) {

        }

        @Override
        public void confirmObjectiveChoice(int cardID) {

        }

        @Override
        public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide, EnumMap<Resource, Integer> ownedResources, List<GenericPair<Integer, Integer>> openCorners, int points) {

        }

        @Override
        public void receiveObjectiveChoice(List<Integer> cardIDs) {

        }

        @Override
        public void receiveCard(List<Integer> cardIDs) {

        }

        @Override
        public void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements) {

        }

        @Override
        public void toggleActive(String nickname) {

        }

        @Override
        public void transition(int round, int currentPlayerIndex) {

        }

        @Override
        public void pauseGame() {

        }

        @Override
        public void endGame(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections) {

        }

        @Override
        public void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate) {

        }
    };


    @BeforeEach
    void setGameParameters() throws Exception {
        player1 = new Player("Sacri");
        player2 = new Player("Piants");
        lobby = new Lobby(player1, 2);
        lobby.addPlayer(player2);
        game = new Game(lobby);
        ConnectionController controller = ConnectionController.getInstance();
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        });
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
        objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
        });

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
    void placeCardTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.placeCard(client2, new GenericPair<>(1, 1), 1, Side.FRONT);
    }


    @Test
    void leaveLobbyTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.leaveLobby(client2, true);
    }

    @Test
    void pickObjectiveTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.pickObjective(client2, 1);
    }

    @Test
    void drawFromDeckTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.drawFromDeck(client2, "resource");
    }

    @Test
    void drawFromVisibleCardsTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.drawFromVisibleCards(client2, "resource", 1);
    }

    @Test
    void leaveGameTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.leaveGame(client2);
    }

    @Test
    void broadcastMessageTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.broadcastMessage(client2, "HELLO");
    }

    @Test
    void directMessageTest() throws Exception {

        client2 = new NetworkSession(controller) {
            @Override
            protected Listener createListener() {
                return new Listener(
                        command -> {
                            assertInstanceOf(ThrowExceptionCommand.class, command);
                            command.execute(Interface);
                        }
                );
            }
        };

        controller.directMessage(client2, "paolo", "HELLO");
    }


}