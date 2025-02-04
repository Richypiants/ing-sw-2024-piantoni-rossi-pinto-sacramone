package it.polimi.ingsw.gc12.Utilities.JSONParsers.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.gc12.Model.Server.Cards.Card;
import it.polimi.ingsw.gc12.Model.Server.Conditions.CornersCondition;
import it.polimi.ingsw.gc12.Model.Server.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.Model.Server.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Server.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.JSONParsers.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to handle JSON parsing and serialization for various card types and conditions on the server.
 */
public class ServerJSONParser extends JSONParser {

    /**
     * Gson instance configured with custom TypeAdapters for PointsCondition and ResourcesCondition.
     */
    private static final Gson GSON_CARD_BUILDER = new GsonBuilder().registerTypeAdapter(PointsCondition.class, new PointsConditionAdapter())
            .registerTypeAdapter(ResourcesCondition.class, new ResourcesConditionAdapter())
            .create();

    /**
     * Custom TypeAdapter to handle the PointsCondition hierarchy serialization and deserialization.
     */
    private static class PointsConditionAdapter extends TypeAdapter<PointsCondition> {
        //This method is unused and overwritten due to the extends.
        @Override
        public void write(JsonWriter out, PointsCondition condition) {
            // Unused
        }

        @Override
        public PointsCondition read(JsonReader in) throws IOException {
            in.beginObject();
            switch(in.nextName()){
                case "RESOURCE":
                {
                    EnumMap<Resource, Integer> resources = new EnumMap<>(Resource.class);
                    in.beginArray();
                    while(in.hasNext()){
                        in.beginObject();
                        resources.put(conversionHelper(in.nextName()), in.nextInt());
                        in.endObject();
                    }
                    in.endArray();
                    in.endObject();
                    return new ResourcesCondition(resources);
                }
                case "CORNER":
                    in.nextNull();
                    in.endObject();
                    return new CornersCondition();
                case "PATTERN":
                {
                    ArrayList<Triplet<Integer, Integer, Resource>> patterns = new ArrayList<>();
                    in.beginArray();
                    while(in.hasNext()){
                        in.beginObject();
                        Resource resource = conversionHelper(in.nextName());
                        in.beginArray();
                        patterns.add( new Triplet<>(in.nextInt(), in.nextInt(), resource));
                        in.endArray();
                        in.endObject();
                    }
                    in.endArray();
                    in.endObject();
                    return new PatternCondition(patterns);
                }
            }
            return null;
        }
    }

    /**
     * Custom TypeAdapter to handle the ResourcesCondition serialization and deserialization.
     */
    private static class ResourcesConditionAdapter extends TypeAdapter<ResourcesCondition> {
        //This method is unused and only implemented due to the extends.
        @Override
        public void write(JsonWriter out, ResourcesCondition condition) {
        }

        @Override
        public ResourcesCondition read(JsonReader in) throws IOException {
            in.beginObject();
            Map<Resource, Integer> resources = new EnumMap<>(Resource.class);
            while(in.hasNext()){
                resources.put(conversionHelper(in.nextName()), in.nextInt());
            }
            in.endObject();
            return new ResourcesCondition(resources);
        }
    }

    /**
     * Constructs a deck of cards from a JSON file.
     *
     * @param filename The name of the JSON file.
     * @param type The TypeToken representing the generic type.
     * @param <E> The type of the cards in the deck.
     * @return An ArrayList of cards.
     */
    public static <E extends Card> ArrayList<E> deckFromJSONConstructor(String filename, TypeToken<ArrayList<E>> type) {
        try {
            return new ArrayList<>(GSON_CARD_BUILDER.fromJson(
                    new InputStreamReader(Objects.requireNonNull(JSONParser.class.getResourceAsStream(filename))), type)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
