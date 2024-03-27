package it.polimi.ingsw.gc12.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.gc12.ServerModel.Cards.Card;
import it.polimi.ingsw.gc12.ServerModel.Conditions.CornersCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.ServerModel.Conditions.ResourcesCondition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class JSONParser {
    /**
     * Creates an instance of a GsonBuilder with custom directives related to how to parse our PointsCondition
     * interface and ResourceCondition
     */
    private static final Gson GSON_CARD_BUILDER = new GsonBuilder().registerTypeAdapter(PointsCondition.class, new PointsConditionAdapter())
            .registerTypeAdapter(ResourcesCondition.class, new ResourcesConditionAdapter())
            .create();

    /**
     *     Generic method which returns an ArrayList<Card> made of a specific card hierarchy subtype, provided the
     *     filename and the TypeToken which represents the generic type
     */
    public static <E extends Card> ArrayList<E> deckFromJSONConstructor(String filename, TypeToken<ArrayList<E>> type) {
        try{
            return new ArrayList<>(GSON_CARD_BUILDER.fromJson(Files.newBufferedReader(Paths.get("src/main/java/it/polimi/ingsw/gc12/JSON_Files/" + filename)), type));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *     Helper Method which converts the String in the effective Resource.ENUM
     */
    private static Resource conversionHelper(String resource){
        return switch (resource) {
            case "MUSHROOM" -> Resource.MUSHROOM;
            case "WOLF" -> Resource.WOLF;
            case "GRASS" -> Resource.GRASS;
            case "BUTTERFLY" -> Resource.BUTTERFLY;
            case "SCROLL" -> Resource.SCROLL;
            case "INK" -> Resource.INK;
            case "FEATHER" -> Resource.FEATHER;
            default -> null;
        };
    }

    /**
     * Custom TypeAdapter to handle the PointsCondition hierarchy serialization and deserialization
     */
    private static class PointsConditionAdapter extends TypeAdapter<PointsCondition> {
        //This method is unused and only implemented due to the extends.
        @Override
        public void write(JsonWriter out, PointsCondition condition) throws IOException {
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
     *     Custom TypeAdapter to handle the ResourceCondition serialization and deserialization
     */
    private static class ResourcesConditionAdapter extends TypeAdapter<ResourcesCondition> {
        //This method is unused and only implemented due to the extends.
        @Override
        public void write(JsonWriter out, ResourcesCondition condition) throws IOException {
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
}


