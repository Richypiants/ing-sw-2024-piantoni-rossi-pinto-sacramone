package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectiveCardTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
    });
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
    });
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
    });
    ArrayList<ObjectiveCard> objectiveCards = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>() {
    });

    @Test
    void awardPoints() throws InvalidCardPositionException, NotEnoughResourcesException, CardNotInHandException {

        Player player = new Player("SACRI");

        GameLobby lobby = new GameLobby(player, 1);
        Game game = new Game(lobby);

        InGamePlayer playerGame = game.getPlayers().getFirst();

        playerGame.setSecretObjective(objectiveCards.get(15));

        playerGame.addCardToHand(initialCards.getFirst());
        playerGame.placeCard(new GenericPair<>(0, 0), playerGame.getCardsInHand().getFirst(), Side.BACK);

        playerGame.addCardToHand(resourceCards.get(4));
        playerGame.placeCard(new GenericPair<>(1, -1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        playerGame.addCardToHand(goldCards.get(30));
        playerGame.placeCard(new GenericPair<>(-1, 1), playerGame.getCardsInHand().getFirst(), Side.FRONT);

        assertEquals(2, playerGame.getSecretObjective().awardPoints(playerGame));
    }
}