package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Controller.Server.ServerControllerTest.createNetworkSessionStub;
import static org.junit.jupiter.api.Assertions.*;

class ClientGameTest {
    static Player testedPlayer;
    static Lobby lobby;
    static Game game;
    static int numOfPlayers;
    static int numOfCardsInHand;
    static int numOfPlacedResources;
    static int numOfPlacedGold;
    static int numOfPlacedCommonObjectives;
    static ClientGame clientGame;
    static GameController gameController;
    static NetworkSession client1;
    static NetworkSession client2;
    ViewModel viewModel = new ViewModel();

    @BeforeAll
    static void setGameParameters() throws Exception {

        Player player1 = new Player("playerOne");
        Player player2 = new Player("playerTwo");

        UUID lobbyUUID = UUID.randomUUID();

        lobby = new Lobby(lobbyUUID, player1, 2);
        lobby.addPlayer(player2);

        game = new Game(lobby);
        gameController = GameController.MODEL.createGameController(game);
        numOfPlayers = game.getPlayersNumber();

        client1 = createNetworkSessionStub(gameController);
        client2 = createNetworkSessionStub(gameController);

        gameController.putActivePlayer(client1, game.getPlayers().get(0));
        gameController.putActivePlayer(client2, game.getPlayers().get(1));
        gameController.getCurrentState().transition();

        int i = 0;
        for (var target : game.getPlayers()) {
            game.placeCard(target, new GenericPair<>(0,0), target.getCardsInHand().getFirst(), Side.FRONT);
        }
        numOfCardsInHand = game.getPlayers().getFirst().getCardsInHand().size();
        numOfPlacedResources = game.getPlacedResources().length;
        numOfPlacedGold = game.getPlacedResources().length;
        numOfPlacedCommonObjectives = game.getCommonObjectives().length;

        testedPlayer = game.getPlayers().getFirst();
        clientGame = game.generateDTO( game.getPlayers().getFirst());
    }

    @Test
    void assertionOnPlayers(){
        ArrayList<ClientPlayer> clientPlayers = clientGame.getPlayers();
        assertEquals(numOfPlayers, clientPlayers.size());

        assertInstanceOf(ClientPlayer.class , clientGame.getThisPlayer());
        assertEquals(testedPlayer.getNickname() , clientGame.getThisPlayer().getNickname());
    }

    @Test
    void correctCardsInOwnHand(){
        assertEquals(numOfCardsInHand, clientGame.getCardsInHand().size());
        for(var card : clientGame.getCardsInHand())
            assertInstanceOf(ClientCard.class, card);
    }

    @Test
    void operationsOnOwnHand(){
        clientGame.removeCardFromHand(clientGame.getCardsInHand().getFirst());
        assertEquals(numOfCardsInHand-1 , clientGame.getCardsInHand().size());

        ClientCard cardToAdd = ViewModel.CARDS_LIST.get(40);

        clientGame.addCardToHand(cardToAdd);
        assertEquals(numOfCardsInHand , clientGame.getCardsInHand().size());

        assertEquals(cardToAdd, clientGame.getCardsInHand().get(numOfCardsInHand-1));
    }

    @Test
    void operationsOnCommonCards(){
        assertEquals(numOfPlacedResources, clientGame.getPlacedResources().length);
        assertEquals(numOfPlacedGold, clientGame.getPlacedGolds().length);
        assertEquals(numOfPlacedCommonObjectives, clientGame.getCommonObjectives().length);

        ClientCard chosenTopResource = ViewModel.CARDS_LIST.get(30);
        ClientCard chosenTopGold = ViewModel.CARDS_LIST.get(50);

        clientGame.setTopDeckResourceCard(chosenTopResource);
        clientGame.setTopDeckGoldCard(chosenTopGold);
        assertEquals(chosenTopResource, clientGame.getTopDeckResourceCard());
        assertEquals(chosenTopGold, clientGame.getTopDeckGoldCard());


        int GOLD_CARD_OFFSET = 41;
        int OBJECTIVE_CARD_OFFSET = 87;
        for(int index = 0; index < 2; index++) {
            ClientCard chosenReplacedResource = ViewModel.CARDS_LIST.get(index);
            ClientCard chosenReplacedGold = ViewModel.CARDS_LIST.get(index + GOLD_CARD_OFFSET);
            ClientCard chosenReplacedObjective = ViewModel.CARDS_LIST.get(index + OBJECTIVE_CARD_OFFSET);

            clientGame.setPlacedResources(chosenReplacedResource, index);
            clientGame.setPlacedGold(chosenReplacedGold, index);
            clientGame.setCommonObjectives(chosenReplacedObjective, index);

            assertEquals(chosenReplacedResource, clientGame.getPlacedResources()[index]);
            assertEquals(chosenReplacedGold, clientGame.getPlacedGolds()[index]);
            assertEquals(chosenReplacedObjective, clientGame.getCommonObjectives()[index]);
        }

    }

    @Test
    void assertionsOnOwnObjective(){
        ClientCard chosenObjective = ViewModel.CARDS_LIST.get(100);

        clientGame.setOwnObjective(chosenObjective);
        assertNotNull(clientGame.getOwnObjective());
        assertEquals(chosenObjective, clientGame.getOwnObjective());
    }

    @Test
    void assertionsOnCurrentRound(){
        int randomCurrentRoundNumber = 20;

        clientGame.setCurrentRound(randomCurrentRoundNumber);
        assertEquals(randomCurrentRoundNumber, clientGame.getCurrentRound());
    }

    @Test
    void assertionsOnCurrentPlayerIndex(){
        //Since the game was sent during the initialPhase, there's no currentPlayer at the moment.
        assertEquals(-1, clientGame.getCurrentPlayerIndex());

        int desiredCurrentPlayerIndex = 0;

        clientGame.setCurrentPlayerIndex(desiredCurrentPlayerIndex);
        assertEquals(desiredCurrentPlayerIndex, clientGame.getCurrentPlayerIndex());
    }

    @Test
    void assertionsOnTurnsLeftUntilFinalPhase() {
        //Since the game was sent during the initialPhase, the counter of turns is set to -1.
        assertEquals(-1, clientGame.getTurnsLeftUntilGameEnds());

        int desiredTurnsLeftUntilTheGameEnds = 2;

        clientGame.setTurnsLeftUntilGameEnds(desiredTurnsLeftUntilTheGameEnds);
        assertEquals(desiredTurnsLeftUntilTheGameEnds, clientGame.getTurnsLeftUntilGameEnds());

    }

    @Test
    void operationsOnChatLog(){
        String chatMessage = "Hello World!";

        int chatLogSize = clientGame.getChatLog().size();

        clientGame.addMessageToChatLog(chatMessage);
        assertEquals(chatLogSize+1, clientGame.getChatLog().size());

        assertEquals(chatMessage, clientGame.getChatLog().getFirst());
    }
}
