package it.polimi.ingsw.gc12.Model.ClientModel;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ClientCard {

    /**
     * A unique card identifier to facilitate the card's retrieval
     */
    public final int ID;
    /**
     * The front image for this card
     */
    public final String FRONT_SPRITE;
    /**
     * The back image for this card
     */
    public final String BACK_SPRITE;

    public final Map<Side, ArrayList<ArrayList<Triplet<String, Integer[], Integer>>>> TUI_SPRITES;

    public ClientCard(int id, String front, String back,
                      Map<Side, ArrayList< ArrayList<Triplet<String, Integer[], Integer>>>> tuiSprites) {
        this.ID = id;
        this.FRONT_SPRITE = front;
        this.BACK_SPRITE = back;
        this.TUI_SPRITES = tuiSprites;
    }

    public ArrayList<Ansi> standardAnsi(ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> sequence) {
        ArrayList<Ansi> lines = new ArrayList<>();
        for (var line : sequence) {
            Ansi tmp = Ansi.ansi();
            for (var triplet : line) {
                if (triplet.getY()[0] != -1)
                    tmp = tmp.fg(triplet.getY()[0]);
                if (triplet.getY()[1] != 1)
                    tmp = tmp.bg(triplet.getY()[1]);

                for (int i = 0; i < triplet.getZ(); i++)
                    tmp.a(triplet.getX());

                tmp = tmp.reset();
            }

            lines.add(tmp);
        }

        return lines;
    }

    public ArrayList<Ansi> upscaledAnsi(ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> sequence) {
        ArrayList<Ansi> lines = new ArrayList<>();
        Ansi[] tmp = new Ansi[3];
        for (var line : sequence) {
            for (int i = 0; i < 2; i++)
                tmp[i] = Ansi.ansi();

            for (var triplet : line) {
                for (int i = 0; i < 2; i++) {
                    if (triplet.getY()[0] != -1)
                        tmp[i] = tmp[i].fg(triplet.getY()[0]);
                    if (triplet.getY()[1] != 1)
                        tmp[i] = tmp[i].bg(triplet.getY()[1]);
                }

                for (int i = 0; i < triplet.getZ(); i++)
                    if (triplet.getX().charAt(0) != ' ') {
                        tmp[0].a("   ");
                        tmp[1].a(" " + triplet.getX().charAt(0) + " ");
                        tmp[2].a("   ");
                    } else
                        for (int j = 0; j < 2; j++)
                            tmp[j].a("   ");

                for (int i = 0; i < 2; i++) {
                    tmp[i].reset();
                }

            }
            lines.addAll(Arrays.asList(tmp).subList(0, 2));
        }

        return lines;
    }


    public static void cardTUIConverter(){
        Map<Resource, String> resourceInitialMap = Map.of(Resource.INK, "I", Resource.WOLF, "W", Resource.MUSHROOM,
                "M", Resource.BUTTERFLY, "B", Resource.GRASS, "G", Resource.FEATHER, "F", Resource.SCROLL,
                "S", Resource.EMPTY, " ", Resource.NOT_A_CORNER, "N");

        ArrayList<ResourceCard> rc = JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){});
        ArrayList<GoldCard> gc = JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){});
        ArrayList<InitialCard> ic = JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){});
        ArrayList<ObjectiveCard> oc = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){});

        for(var card : rc){
            //TODO: mappare nel colore giusto di FG e BG
            Integer cardColorFG = card.getCenterBackResources().values().stream().findAny().orElseThrow();
            Integer cardColorBG = card.getCenterBackResources().values().stream().findAny().orElseThrow();
            ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> sequence = new ArrayList<>();

            for(int i = 0; i < 5; i++){
                sequence.add(new ArrayList<>());
            }

            sequence.getFirst().add(new Triplet<>(resourceInitialMap.get(card.getCornerResource(Side.FRONT, -1, 1)), new Integer[]{-1,-1}, 1));
            if(card.POINTS_GRANTED == 0){
                sequence.getFirst().add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, 11));
            } else {
                sequence.getFirst().add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, 5));
                sequence.getFirst().add(new Triplet<>(String.valueOf(card.POINTS_GRANTED), new Integer[]{-1, -1}, 1));
                sequence.getFirst().add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, 11));
            }
            sequence.getFirst().add(new Triplet<>(resourceInitialMap.get(card.getCornerResource(Side.FRONT, 1, 1)), new Integer[]{-1,-1}, 1));

            sequence.get(1).add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, 13));

            sequence.get(2).add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, card.getCenterBackResources().size()));

            for(var entry : card.getCenterBackResources().entrySet())
                sequence.get(2).add(new Triplet<>(resourceInitialMap.get(entry.getKey()), new Integer[]{-1,-1}, entry.getValue()));

            sequence.get(2).add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, card.getCenterBackResources().size()));

            sequence.get(3).add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, 13));

            sequence.get(4).add(new Triplet<>(resourceInitialMap.get(card.getCornerResource(Side.FRONT, -1, -1)), new Integer[]{-1,-1}, 1));
            sequence.get(4).add(new Triplet<>(" ", new Integer[]{cardColorFG, cardColorBG}, 11));
            sequence.get(4).add(new Triplet<>(resourceInitialMap.get(card.getCornerResource(Side.FRONT, 1, -1)), new Integer[]{-1,-1}, 1));
        }
    }

}
