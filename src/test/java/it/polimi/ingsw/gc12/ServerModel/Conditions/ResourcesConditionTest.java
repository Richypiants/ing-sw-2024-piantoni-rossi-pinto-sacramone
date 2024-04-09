package it.polimi.ingsw.gc12.ServerModel.Conditions;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ResourcesConditionTest {

    @Test
    void numberOfTimesSatisfied() {
        ArrayList<Card> array = new ArrayList<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<ArrayList<ResourceCard>>() {
        }));
        ArrayList<Card> array1 = new ArrayList<>(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        ArrayList<Card> array2 = new ArrayList<>(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<InitialCard>>() {
        }));


    }
}