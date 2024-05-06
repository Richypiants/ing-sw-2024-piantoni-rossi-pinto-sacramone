package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlayableCardTest {
    private static ArrayList<ResourceCard> resourceCards;
    private static ArrayList<GoldCard> goldCards;
    private static ArrayList<InitialCard> initialCards;

    @BeforeAll
    static void setCardsLists() {
        resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        });
        goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        });
        initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
        });
    }

    @Test
    void getCornerResource() {

        assertEquals(Resource.PLANT, initialCards.get(0).getCornerResource(Side.BACK, 1, 1));
        assertEquals(Resource.INSECT, initialCards.get(0).getCornerResource(Side.BACK, -1, -1));

        assertEquals(1, initialCards.get(0).getCenterBackResources().get(Resource.INSECT));
        assertNull(initialCards.get(0).getCenterBackResources().get(Resource.PLANT));
        assertNull(initialCards.get(0).getCenterBackResources().get(Resource.FUNGI));
        assertNull(initialCards.get(0).getCenterBackResources().get(Resource.ANIMAL));

        assertEquals(Resource.FUNGI, resourceCards.get(0).getCornerResource(Side.FRONT, -1, 1));
        assertEquals(Resource.FUNGI, resourceCards.get(0).getCornerResource(Side.FRONT, -1, -1));
        assertEquals(Resource.NOT_A_CORNER, resourceCards.get(0).getCornerResource(Side.FRONT, 1, -1));
        assertEquals(Resource.EMPTY, resourceCards.get(0).getCornerResource(Side.FRONT, 1, 1));

        assertEquals(Resource.NOT_A_CORNER, goldCards.get(0).getCornerResource(Side.FRONT, -1, 1));
        assertEquals(Resource.EMPTY, goldCards.get(0).getCornerResource(Side.FRONT, -1, -1));
        assertEquals(Resource.QUILL, goldCards.get(0).getCornerResource(Side.FRONT, 1, -1));
        assertEquals(Resource.EMPTY, goldCards.get(0).getCornerResource(Side.FRONT, 1, 1));
    }
}