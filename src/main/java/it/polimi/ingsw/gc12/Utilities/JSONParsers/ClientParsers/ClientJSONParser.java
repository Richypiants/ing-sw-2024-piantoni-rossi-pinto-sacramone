package it.polimi.ingsw.gc12.Utilities.JSONParsers.ClientParsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.JSONParsers.JSONParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility class to handle JSON parsing and serialization for various card types on the client.
 */
public class ClientJSONParser extends JSONParser {

    private static final Gson CARD_IMAGE_RESOURCES_BUILDER = new Gson();

    /**
     * Reads ClientCard objects from a JSON file.
     *
     * @param filename The name of the JSON file.
     * @return An ArrayList of ClientCard objects.
     */
    public static ArrayList<ClientCard> generateClientCardsFromJSON(String filename) {
        return new ArrayList<>(CARD_IMAGE_RESOURCES_BUILDER.fromJson(
                new InputStreamReader(Objects.requireNonNull(JSONParser.class.getResourceAsStream(filename))),
                new TypeToken<ArrayList<ClientCard>>() {
                })
        );
    }
}
