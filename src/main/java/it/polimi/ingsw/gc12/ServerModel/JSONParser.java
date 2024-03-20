package it.polimi.ingsw.gc12.ServerModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;

public class JSONParser {

    //TODO: Choose the library that fits best to our problem (GSON, Jackson).
    //This could be intended as the wrapper class around the calls to the real library
    public static ArrayList<Card> fromJSONtoCardDeckConstructor(){
        return null;
    }


    //TODO: REMOVE ALL PRINTS
    public void cardsBuilder(){
        try{
            Gson gson = new Gson();
            Reader resourceReader = Files.newBufferedReader(Paths.get("src/main/java/it/polimi/ingsw/gc12/JSON_Files/resource_cards.json"));
            ArrayList<ResourceCard> resourceCards = gson.fromJson(resourceReader, new TypeToken<ArrayList<ResourceCard>>(){});

            Reader goldReader = Files.newBufferedReader(Paths.get("src/main/java/it/polimi/ingsw/gc12/JSON_Files/test.json"));

            Gson builder = new GsonBuilder().registerTypeHierarchyAdapter(PointsCondition.class, new PointsConditionAdapter()).create();
            ArrayList<GoldCard> goldCards = builder.fromJson(goldReader, new TypeToken<ArrayList<GoldCard>>(){});

            Reader objectiveReader = Files.newBufferedReader(Paths.get("src/main/java/it/polimi/ingsw/gc12/JSON_Files/objective_cards.json"));
            ArrayList<ObjectiveCard> objectiveCards = builder.fromJson(objectiveReader, new TypeToken<ArrayList<ObjectiveCard>>(){});
            System.out.println(objectiveCards.get(0).POINTS_CONDITION);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static class PointsConditionAdapter extends TypeAdapter<PointsCondition> {
        //This method is unused and only implemented due to the extends.
        @Override
        public void write(JsonWriter out, PointsCondition condition) throws IOException {
            //NOT IMPLEMENTED LOGIC
            out.value(condition.toString());
        }

        public static Resource conversionHelper(String resource){
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
        @Override
        public PointsCondition read(JsonReader in) throws IOException {
            Gson gson = new Gson();
            in.beginObject();
            System.out.println("USING: ");
            switch(in.nextName()){
                case "RESOURCE":
                {
                    System.out.println("RESOURCE");
                    EnumMap<Resource, Integer> resources = new EnumMap<Resource, Integer>(Resource.class);
                    in.beginArray();
                    while(in.hasNext()){
                        in.beginObject();
                        resources.put(conversionHelper(in.nextName()), (Integer) in.nextInt());
                        System.out.println("KEYS:" + resources.keySet());
                        in.endObject();
                    }
                    in.endArray();
                    in.endObject();
                    return new ResourcesCondition(resources);
                }
                case "CORNER":
                    System.out.println("CORNER");
                    in.endObject();
                    return new CornersCondition();
                case "PATTERN":
                {
                    System.out.println("PATTERN");
                    ArrayList<Triplet<Integer, Integer, Resource>> patterns = new ArrayList<>();
                    in.beginArray();
                    while(in.hasNext()){
                        in.beginObject();
                        Resource resource = conversionHelper(in.nextName());
                        in.beginArray();
                        patterns.add( new Triplet<>((Integer) in.nextInt(), (Integer) in.nextInt(), resource));
                        in.endArray();
                        System.out.println(patterns.get(0).getX());
                        System.out.println(patterns.get(0).getY());
                        System.out.println(patterns.get(0).getZ());
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






    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        parser.cardsBuilder();
    }
}


