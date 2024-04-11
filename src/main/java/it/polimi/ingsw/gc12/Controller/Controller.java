package it.polimi.ingsw.gc12.Controller;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Controller {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup(); //FIXME: why does publicLookup() not work?
    public static final Map<String, MethodHandle> commandHandles = createHandles();
    public static final Map<Integer, Card> cardsList = loadCards();

    //TODO: keep lambda or not?
    private static Map<String, MethodHandle> createHandles() {
        return Arrays.stream(ClientController.class.getDeclaredMethods())
                .filter((method) -> Modifier.isPublic(method.getModifiers()))
                .map((method) -> {
                            try {
                                return new GenericPair<>(method.getName(), lookup.unreflect(method));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        /*MethodType type = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                        MethodHandle thisMethod = lookup.findVirtual(GameCommand.class, method.getName(), type);
                        */
                )
                .collect(Collectors.toUnmodifiableMap(GenericPair::getX, GenericPair::getY)
                );
    }

    private static Map<Integer, Card> loadCards() {
        //TODO: map of maps?
        Map<Integer, Card> tmp = new HashMap<>();
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<>(){}))
                .forEach((card) -> tmp.put(card.ID, card));

        return Collections.unmodifiableMap(tmp);
    }
}
