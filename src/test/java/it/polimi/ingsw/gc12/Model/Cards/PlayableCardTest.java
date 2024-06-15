package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayableCardTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.RESOURCE_DECK_FILENAME, new TypeToken<>(){});
        goldCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.GOLD_DECK_FILENAME, new TypeToken<>(){});
        initialCards = CardDeckTest.loadCardDeckAsArrayList(CardDeckTest.INITIAL_DECK_FILENAME, new TypeToken<>(){});
    }

    @Test
    void getCornerResource() {
        assertEquals(Resource.PLANT, initialCards.getFirst().getCornerResource(Side.BACK, 1, 1));
        assertEquals(Resource.INSECT, initialCards.getFirst().getCornerResource(Side.BACK, -1, -1));

        assertEquals(1, initialCards.getFirst().getCenterBackResources().get(Resource.INSECT));
        assertNull(initialCards.getFirst().getCenterBackResources().get(Resource.PLANT));
        assertNull(initialCards.getFirst().getCenterBackResources().get(Resource.FUNGI));
        assertNull(initialCards.getFirst().getCenterBackResources().get(Resource.ANIMAL));

        assertEquals(Resource.FUNGI, resourceCards.getFirst().getCornerResource(Side.FRONT, -1, 1));
        assertEquals(Resource.FUNGI, resourceCards.getFirst().getCornerResource(Side.FRONT, -1, -1));
        assertEquals(Resource.NOT_A_CORNER, resourceCards.getFirst().getCornerResource(Side.FRONT, 1, -1));
        assertEquals(Resource.EMPTY, resourceCards.getFirst().getCornerResource(Side.FRONT, 1, 1));

        assertEquals(Resource.NOT_A_CORNER, goldCards.getFirst().getCornerResource(Side.FRONT, -1, 1));
        assertEquals(Resource.EMPTY, goldCards.getFirst().getCornerResource(Side.FRONT, -1, -1));
        assertEquals(Resource.QUILL, goldCards.getFirst().getCornerResource(Side.FRONT, 1, -1));
        assertEquals(Resource.EMPTY, goldCards.getFirst().getCornerResource(Side.FRONT, 1, 1));
    }

    @Test
    void awardPointsOfCardPlacedOnBackTest(){
        Player player = new Player("TestPlayer");
        Lobby lobby = new Lobby(UUID.randomUUID(), player, 1);
        Game game = new Game(lobby);
        PlayableCard targetPlacedCard = initialCards.getFirst();

        InGamePlayer target = game.getPlayers().getFirst();
        target.addCardToHand(targetPlacedCard);
        assertDoesNotThrow(() -> game.placeCard(target, new GenericPair<>(0,0), target.getCardsInHand().getFirst(), Side.BACK));
        assertEquals(0, targetPlacedCard.awardPoints(target));
    }
}