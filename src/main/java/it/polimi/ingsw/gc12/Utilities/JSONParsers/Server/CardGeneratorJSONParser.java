package it.polimi.ingsw.gc12.Utilities.JSONParsers.Server;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Server.Conditions.CornersCondition;
import it.polimi.ingsw.gc12.Model.Server.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Server.Conditions.ResourcesCondition;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Utility class to generate JSON files by parsing the objects on the server
 * when modifications to the graphical resources of cards on the TUI are required.
 */
public class CardGeneratorJSONParser extends ServerJSONParser {

    /**
     * Generates a JSON file containing only the playable ClientCard objects.
     */
    public static void generateClientCardsJSONPlayableOnly() {
        ArrayList<ResourceCard> rc = deckFromJSONConstructor(
                "/jsonFiles/Server/resource_cards.json",
                new TypeToken<>() {
                }
        );
        ArrayList<GoldCard> gc = deckFromJSONConstructor(
                "/jsonFiles/Server/gold_cards.json",
                new TypeToken<>() {
                }
        );
        ArrayList<InitialCard> ic = deckFromJSONConstructor(
                "/jsonFiles/Server/initial_cards.json",
                new TypeToken<>() {
                }
        );
        //ArrayList<ObjectiveCard> oc = JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){});

        ArrayList<ClientCard> clientCards = new ArrayList<>();

        assert rc != null;
        assert gc != null;
        assert ic != null;
        for (var card : rc) {
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

        for (var card : gc) {
            clientCards.add(new ClientCard(card.ID,
                            Map.of(
                                    Side.FRONT, "/images/cards/front/0" + ((card.ID < 10) ? "0" + card.ID : card.ID) + ".png",
                                    Side.BACK, "/images/cards/back/" + card.getCenterBackResources().keySet().stream().findAny().orElseThrow().SYMBOL + "_gold.png"
                            ),
                            Map.of(
                                    Side.FRONT, generatePlayableCardTUISprite(card, Side.FRONT),
                                    Side.BACK, generatePlayableCardTUISprite(card, Side.BACK)
                            )
                    )
            );
        }
        for (var card : ic) {
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
                    new FileWriter("src/main/java/it/polimi/ingsw/gc12/Utilities/jsonFiles/client_cards.json")
            );
        } catch (IOException e) {
            throw new RuntimeException(e); //Should never happen
        }
    }

    /**
     * Generates a playable card TUI sprite.
     *
     * @param card The playable card.
     * @param side The side of the card.
     * @return A sequence of TUI sprites.
     */
    private static ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> generatePlayableCardTUISprite(PlayableCard card, Side side) {
        int cardColor = (card instanceof InitialCard) ? 214 : card.getCenterBackResources().keySet().stream().findAny().orElseThrow().ANSI_COLOR;
        Resource cornerResource;

        ArrayList<ArrayList<Triplet<String, Integer[], Integer>>> sequence = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            sequence.add(new ArrayList<>());
        }

        cornerResource = card.getCornerResource(side, -1, 1);
        sequence.getFirst().add(new Triplet<>(cornerResource.SYMBOL,
                new Integer[]{cornerResource.ANSI_COLOR,
                        cornerResource.equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                }, 1)
        );
        if (side.equals(Side.BACK) || card.POINTS_GRANTED == 0)
            sequence.getFirst().add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 11));
        else if (card instanceof GoldCard && ((GoldCard) card).getPointsCondition() != null) {
            PointsCondition cardCondition = ((GoldCard) card).getPointsCondition();
            String stringForCondition;
            int colorForCondition = Resource.EMPTY.ANSI_COLOR;
            if (cardCondition instanceof CornersCondition)
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

        if (side.equals(Side.BACK)) {
            int numberOfResources = card.getCenterBackResources().values().stream().mapToInt((value) -> value).sum();
            sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (13 - numberOfResources) / 2));


            boolean needsMiddleSpace = card.getCenterBackResources().size() == 2;

            for (var entry : card.getCenterBackResources().entrySet()) {
                sequence.get(2).add(new Triplet<>(entry.getKey().SYMBOL,
                        new Integer[]{entry.getKey().ANSI_COLOR,
                                entry.getKey().equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                        }, 1));
                if (needsMiddleSpace) {
                    sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, -1}, 1));
                    needsMiddleSpace = false;
                }
            }
            sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (13 - numberOfResources) / 2));
        } else {
            sequence.get(2).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 13));
        }

        sequence.get(3).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, 13));

        cornerResource = card.getCornerResource(side, -1, -1);
        sequence.get(4).add(new Triplet<>(cornerResource.SYMBOL,
                new Integer[]{cornerResource.ANSI_COLOR,
                        cornerResource.equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                }, 1));
        if (side.equals(Side.FRONT) && card instanceof GoldCard && ((GoldCard) card).getNeededResourcesToPlay() != null) {
            Map<Resource, Integer> neededResources = ((GoldCard) card).getNeededResourcesToPlay().getConditionParameters();
            int numberOfResources = neededResources.values().stream().mapToInt((value) -> value).sum();
            sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (11 - numberOfResources) / 2));

            boolean trailingSpace = (numberOfResources == 4);

            for (var entry : neededResources.entrySet())
                sequence.get(4).add(new Triplet<>(entry.getKey().SYMBOL,
                        new Integer[]{entry.getKey().ANSI_COLOR,
                                entry.getKey().equals(Resource.NOT_A_CORNER) ? cardColor : Resource.EMPTY.ANSI_COLOR
                        }, entry.getValue()));

            if (trailingSpace)
                sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, -1}, 1));

            sequence.get(4).add(new Triplet<>(" ", new Integer[]{-1, cardColor}, (11 - numberOfResources) / 2));
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

    /**
     * Method used to rebuild .JSON files while updating some characteristics of the cards viewed on the TUI such as background color or letters.
     * For some reason, the last generated PlayableCard is truncated, so it is safe to maintain a copy of the old file and fix it manually.
     */
    public static void main(String[] args) {
        generateClientCardsJSONPlayableOnly();
    }
}
