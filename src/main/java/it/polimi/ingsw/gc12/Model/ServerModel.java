package it.polimi.ingsw.gc12.Model;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Controller.ServerController.LobbyController;
import it.polimi.ingsw.gc12.Listeners.Listenable;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.Exceptions.FullLobbyException;
import it.polimi.ingsw.gc12.Utilities.JSONParser;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ServerModel implements Listenable {

    public static final Map<Integer, Card> cardsList = loadModelCards();
    public static final Map<Integer, ClientCard> clientCardsList = loadClientCards();

    public final ReentrantReadWriteLock LOBBY_CONTROLLERS_LOCK;
    public final ReentrantReadWriteLock GAME_CONTROLLERS_LOCK;
    private final Map<UUID, LobbyController> LOBBY_CONTROLLERS;
    private final Map<UUID, GameController> GAME_CONTROLLERS;
    public final List<Listener> LOBBIES_LISTENERS;

    public ServerModel() {
        LOBBY_CONTROLLERS = new HashMap<>();
        LOBBY_CONTROLLERS_LOCK = new ReentrantReadWriteLock(true);
        GAME_CONTROLLERS = new HashMap<>();
        GAME_CONTROLLERS_LOCK = new ReentrantReadWriteLock(true);
        LOBBIES_LISTENERS = new ArrayList<>();
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

    public LobbyController getLobbyController(UUID lobbyUUID) {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            return LOBBY_CONTROLLERS.get(lobbyUUID);
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    public Map<UUID, Lobby> getLobbiesMap() {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            return LOBBY_CONTROLLERS.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, (entry) -> (entry.getValue()).CONTROLLED_LOBBY));
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    public LobbyController createLobbyController(Lobby lobby) {
        LobbyController createdController = new LobbyController(lobby);
        LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            LOBBY_CONTROLLERS.put(lobby.getRoomUUID(), createdController);
            notifyListeners(new UpdateLobbyCommand(lobby));
            return createdController;
        } finally {
            LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    public void destroyLobbyController(LobbyController controller) {
        LOBBY_CONTROLLERS_LOCK.writeLock().lock();
        try {
            Lobby lobby = controller.CONTROLLED_LOBBY;
            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & Lobby?
            while (lobby.getPlayersNumber() > 0)
                lobby.removePlayer(lobby.getPlayers().getFirst());

            LOBBY_CONTROLLERS.remove(lobby.getRoomUUID());
            notifyListeners(new UpdateLobbyCommand(lobby));
        } finally {
            LOBBY_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    public void addPlayerToLobby(Player target, UUID lobbyUUID) throws FullLobbyException {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            LobbyController controller = LOBBY_CONTROLLERS.get(lobbyUUID);
            if (controller == null) throw new IllegalArgumentException();
            controller.CONTROLLED_LOBBY.addPlayer(target);
            notifyListeners(new UpdateLobbyCommand(controller.CONTROLLED_LOBBY));
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    public void removePlayerFromLobby(Player target, Lobby lobby) {
        LOBBY_CONTROLLERS_LOCK.readLock().lock();
        try {
            lobby.removePlayer(target);
            notifyListeners(new UpdateLobbyCommand(lobby));
        } finally {
            LOBBY_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    public GameController getGameController(UUID gameUUID) {
        GAME_CONTROLLERS_LOCK.readLock().lock();
        try {
            return GAME_CONTROLLERS.get(gameUUID);
        } finally {
            GAME_CONTROLLERS_LOCK.readLock().unlock();
        }
    }

    public GameController createGameController(Game game) {
        GameController createdController = new GameController(game);
        GAME_CONTROLLERS_LOCK.writeLock().lock();
        try {
            GAME_CONTROLLERS.put(game.getRoomUUID(), createdController);
            return createdController;
        } finally {
            GAME_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    public void destroyGameController(GameController controller) {
        GAME_CONTROLLERS_LOCK.writeLock().lock();
        try {
            GAME_CONTROLLERS.remove(controller.CONTROLLED_GAME.getRoomUUID());
        } finally {
            GAME_CONTROLLERS_LOCK.writeLock().unlock();
        }
    }

    @Override
    public void addListener(Listener listener) {
        synchronized (LOBBIES_LISTENERS) {
            LOBBIES_LISTENERS.add(listener);
        }
    }

    @Override
    public void removeListener(Listener listener) {
        synchronized (LOBBIES_LISTENERS) {
            LOBBIES_LISTENERS.remove(listener);
        }
    }

    @Override
    public void notifyListeners(ClientCommand command) {
        synchronized (LOBBIES_LISTENERS) {
            for (var listener : LOBBIES_LISTENERS)
                listener.notified(command);
        }
    }
}
