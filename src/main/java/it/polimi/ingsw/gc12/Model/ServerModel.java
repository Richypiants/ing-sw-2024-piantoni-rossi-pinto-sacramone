package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Listeners.Listenable;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

public class ServerModel implements Listenable {

    public static final Map<Integer, Card> cardsList = loadModelCards();
    public static final Map<Integer, ClientCard> clientCardsList = loadClientCards();

    public final Map<UUID, Room> ROOMS;
    public final List<Listener> ROOMS_LISTENERS;

    public ServerModel() {
        ROOMS = new HashMap<>();
        ROOMS_LISTENERS = new ArrayList<>();
    }

    private static Map<Integer, Card> loadModelCards() {
        //TODO: map of maps?
        Map<Integer, Card> tmp = new HashMap<>();
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("resource_cards.json",
                        new TypeToken<ArrayList<ResourceCard>>() {
                        }))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json",
                        new TypeToken<ArrayList<GoldCard>>() {
                        }))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json",
                        new TypeToken<ArrayList<InitialCard>>() {
                        }))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json",
                        new TypeToken<ArrayList<ObjectiveCard>>() {
                        }))
                .forEach((card) -> tmp.put(card.ID, card));

        return Collections.unmodifiableMap(tmp);
    }

    private static Map<Integer, ClientCard> loadClientCards() {
        return JSONParser.generateClientCardsFromJSON("client_cards.json")
                .stream().collect(Collectors.toMap((card) -> card.ID, (card) -> card));
    }

    @Override
    public void addListener(Listener listener) {
        ROOMS_LISTENERS.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        ROOMS_LISTENERS.remove(listener);
    }

    @Override
    public void notifyListeners() {
        for (var listener : ROOMS_LISTENERS)
            listener.notified();
    }
}
