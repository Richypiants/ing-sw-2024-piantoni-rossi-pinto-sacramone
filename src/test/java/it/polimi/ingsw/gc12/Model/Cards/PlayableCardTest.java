package it.polimi.ingsw.gc12.Model.Cards;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayableCardTest {

    ArrayList<ResourceCard> resourceCards = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
    });
    ArrayList<GoldCard> goldCards = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
    });
    ArrayList<InitialCard> initialCards = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>() {
    });

    @Test
    void getCornerResource() {

        assertEquals(Resource.GRASS, initialCards.get(0).getCornerResource(Side.BACK, 1, 1));
        assertEquals(Resource.BUTTERFLY, initialCards.get(0).getCornerResource(Side.BACK, -1, -1));

        // FIXME: Check initial_cards.json of getCenterBackResources()
        // assertEquals(Resource.BUTTERFLY, initialCards.get(0).getCenterBackResources());

        assertEquals(Resource.MUSHROOM, resourceCards.get(0).getCornerResource(Side.FRONT, -1, 1));
        assertEquals(Resource.MUSHROOM, resourceCards.get(0).getCornerResource(Side.FRONT, -1, -1));
        assertEquals(Resource.NOT_A_CORNER, resourceCards.get(0).getCornerResource(Side.FRONT, 1, -1));
        assertEquals(Resource.EMPTY, resourceCards.get(0).getCornerResource(Side.FRONT, 1, 1));

        assertEquals(Resource.NOT_A_CORNER, goldCards.get(0).getCornerResource(Side.FRONT, -1, 1));
        assertEquals(Resource.EMPTY, goldCards.get(0).getCornerResource(Side.FRONT, -1, -1));
        assertEquals(Resource.FEATHER, goldCards.get(0).getCornerResource(Side.FRONT, 1, -1));
        assertEquals(Resource.EMPTY, goldCards.get(0).getCornerResource(Side.FRONT, 1, 1));
    }


}