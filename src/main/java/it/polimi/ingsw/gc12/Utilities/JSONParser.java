package it.polimi.ingsw.gc12.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.Conditions.CornersCondition;
import it.polimi.ingsw.gc12.Model.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.Model.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Conditions.ResourcesCondition;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

//TODO: parse ClientCard correctly (add custom parser)
public class JSONParser {
    /**
     * Creates an instance of a GsonBuilder with custom directives related to how to parse our PointsCondition
     * interface and ResourceCondition
     */
    private static final Gson GSON_CARD_BUILDER = new GsonBuilder().registerTypeAdapter(PointsCondition.class, new PointsConditionAdapter())
            .registerTypeAdapter(ResourcesCondition.class, new ResourcesConditionAdapter())
            .create();

    private static final Gson CARD_IMAGE_RESOURCES_BUILDER = new Gson();

    /**
     *     Generic method which returns an ArrayList<Card> made of a specific card hierarchy subtype, provided the
     *     filename and the TypeToken which represents the generic type
     */
    public static <E extends Card> ArrayList<E> deckFromJSONConstructor(String filename, TypeToken<ArrayList<E>> type) {
        try{
            return new ArrayList<>(GSON_CARD_BUILDER.fromJson(Files.newBufferedReader(Paths.get("src/main/java/it/polimi/ingsw/gc12/Utilities/json_files/" + filename)), type));
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
            case "FUNGI" -> Resource.FUNGI;
            case "ANIMAL" -> Resource.ANIMAL;
            case "PLANT" -> Resource.PLANT;
            case "INSECT" -> Resource.INSECT;
            case "SCROLL" -> Resource.SCROLL;
            case "INK" -> Resource.INK;
            case "QUILL" -> Resource.QUILL;
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

    public static ArrayList<ClientCard> clientCardsFromJSON(String filename) {
        try {
            return new ArrayList<>(CARD_IMAGE_RESOURCES_BUILDER.fromJson(
                    Files.newBufferedReader(Paths.get("src/main/java/it/polimi/ingsw/gc12/Utilities/json_files/" + filename)),
                    new TypeToken<ArrayList<ClientCard>>(){}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateClientCardsJSONPlayableOnly() {
        ArrayList<ResourceCard> rc = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){});
        ArrayList<GoldCard> gc = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){});
        ArrayList<InitialCard> ic = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){});
        //ArrayList<ObjectiveCard> oc = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){});

        ArrayList<ClientCard> clientCards = new ArrayList<>();

        assert rc != null;
        assert gc != null;
        assert ic != null;
        for(var card : rc) {
            clientCards.add(new ClientCard(card.ID,
                    Map.of(
                            Side.FRONT, "/images/cards/front/0" + card.ID + ".png",
                            Side.BACK, "/images/cards/back/" + card.getCenterBackResources().keySet().stream().findAny().orElseThrow().SYMBOL + "_resource.png"
                    ),
                            Map.of(
                                    Side.FRONT, generatePlayableCardTUISprite(card, Side.FRONT),
                                    Side.BACK, generatePlayableCardTUISprite(card, Side.BACK)
                            )
                    )
            );
        }

        //FIXME: first ten cards return "01" instead of "001" and so on...
        for(var card : gc) {
            clientCards.add(new ClientCard(card.ID,
                    Map.of(
                            Side.FRONT, "/images/cards/front/0" + card.ID + ".png",
                            Side.BACK, "/images/cards/back/" + card.getCenterBackResources().keySet().stream().findAny().orElseThrow().SYMBOL + "_gold.png"
                    ),
                    Map.of(
                            Side.FRONT, generatePlayableCardTUISprite(card, Side.FRONT),
                            Side.BACK, generatePlayableCardTUISprite(card, Side.BACK)
                    )
                    )
            );
        }
        for(var card : ic) {
            clientCards.add(new ClientCard(card.ID,
                    Map.of(
                            Side.FRONT, "/images/cards/front/0" + card.ID + ".png",
                            Side.BACK, "/images/cards/back/0" + card.ID + "_back.png"
                    ),
                    Map.of(
                            Side.FRONT, generatePlayableCardTUISprite(card, Side.FRONT),
                            Side.BACK, generatePlayableCardTUISprite(card, Side.BACK)
                    )
                    )
            );
        }

        try {
            new GsonBuilder().setPrettyPrinting().create().toJson(clientCards,
                    new FileWriter("src/main/java/it/polimi/ingsw/gc12/Utilities/json_files/client_cards.json")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> generatePlayableCardTUISprite(PlayableCard card, Side side) {
        //TODO: mappare nel colore giusto di FG e BG
        int cardColor = (card instanceof InitialCard) ? 214/*222-255? - 231?*/ : card.getCenterBackResources().keySet().stream().findAny().orElseThrow().ANSI_COLOR;
        Resource cornerResource;

        ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> sequence = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            sequence.add(new ArrayList<>());
        }

        cornerResource = card.getCornerResource(side, -1, 1);
        sequence.getFirst().add(new Triplet<>(cornerResource.SYMBOL,
                new Integer[]{cornerResource.ANSI_COLOR,
                        cornerResource.equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                }, 1)
        );
        if(side.equals(Side.BACK) || card.POINTS_GRANTED == 0)
            sequence.getFirst().add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 11));
        else if(card instanceof GoldCard && ((GoldCard) card).getPointsCondition() != null){
            PointsCondition cardCondition = ((GoldCard) card).getPointsCondition();
            String stringForCondition = "";
            int colorForCondition = Resource.EMPTY.ANSI_COLOR;
            if(cardCondition instanceof CornersCondition)
                stringForCondition = "C";
            else {
                Resource tmp = ((ResourcesCondition) cardCondition).getConditionParameters().keySet().stream().findAny().orElseThrow();
                stringForCondition = tmp.SYMBOL;
                colorForCondition = tmp.ANSI_COLOR;
            }

            sequence.getFirst().add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 3));
            sequence.getFirst().add(new Triplet<>(card.POINTS_GRANTED + "   " + stringForCondition,
                    new Integer[]{colorForCondition, -1}, 1));
            sequence.getFirst().add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 3));
        } else {
            sequence.getFirst().add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 5));
            sequence.getFirst().add(new Triplet<>(String.valueOf(card.POINTS_GRANTED), new Integer[]{-1, -1}, 1));
            sequence.getFirst().add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 5));
        }
        cornerResource = card.getCornerResource(side, 1, 1);
        sequence.getFirst().add(new Triplet<>(cornerResource.SYMBOL,
                        new Integer[]{cornerResource.ANSI_COLOR,
                                cornerResource.equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                        }, 1));

        sequence.get(1).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 13));

        if(side.equals(Side.BACK)) {
            int numberOfResources = card.getCenterBackResources().values().stream().mapToInt((value) -> value).sum();
            sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (13 - numberOfResources) / 2));


            boolean needsMiddleSpace = card.getCenterBackResources().size() == 2;

            for (var entry : card.getCenterBackResources().entrySet()) {
                sequence.get(2).add(new Triplet<>(entry.getKey().SYMBOL,
                        new Integer[]{entry.getKey().ANSI_COLOR,
                                entry.getKey().equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                        }, 1));
                if(needsMiddleSpace){
                    sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, -1}, 1));
                    needsMiddleSpace = false;
                }
            }
            sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (13 - numberOfResources)/2));
        } else {
            sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 13));
        }

        sequence.get(3).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 13));

        cornerResource = card.getCornerResource(side, -1, -1);
        sequence.get(4).add(new Triplet<>(cornerResource.SYMBOL,
                new Integer[]{cornerResource.ANSI_COLOR,
                        cornerResource.equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                }, 1));
        if(side.equals(Side.FRONT) && card instanceof GoldCard && ((GoldCard) card).getNeededResourcesToPlay() != null) {
            Map<Resource, Integer> neededResources = ((GoldCard) card).getNeededResourcesToPlay().getConditionParameters();
            int numberOfResources = neededResources.values().stream().mapToInt((value) -> value).sum();
            sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (11 - numberOfResources)/2));

            boolean trailingSpace = (numberOfResources == 4);

            for (var entry : neededResources.entrySet())
                sequence.get(4).add(new Triplet<>(entry.getKey().SYMBOL,
                        new Integer[]{entry.getKey().ANSI_COLOR,
                                entry.getKey().equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                        }, entry.getValue()));

            if(trailingSpace)
                sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, -1}, 1));

            sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (11 - numberOfResources)/2));
        } else {
            sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 11));
        }
        cornerResource = card.getCornerResource(side, 1, -1);
        sequence.get(4).add(new Triplet<>(cornerResource.SYMBOL,
                new Integer[]{cornerResource.ANSI_COLOR,
                        cornerResource.equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                }, 1));

        return sequence;
    }

    public static void main(String[] args) {
        generateClientCardsJSONPlayableOnly();

        /*AnsiConsole.systemInstall();
        TUIView instance = TUIView.getInstance();
        ArrayList<ClientCard> test = clientCardsFromJSON("client_cards.json");

        for(int i = 86; i < 102; i++) {
            System.out.println(instance.standardAnsi(test.get(i), Side.BACK));
            System.out.println();
        }*/

    }
}
