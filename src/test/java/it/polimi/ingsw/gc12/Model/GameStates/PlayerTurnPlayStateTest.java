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
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.UUID;

class PlayerTurnPlayStateTest {
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
}