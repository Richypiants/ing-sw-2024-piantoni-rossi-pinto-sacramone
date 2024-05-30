package it.polimi.ingsw.gc12.Controller.ServerController;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotExistingPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*TODO: In case of high traffic volumes on network, we can reduce it by sending the updates to lobby states (creation, updates) only to clients
        which aren't already in a lobby. */
public abstract class ServerController implements ServerControllerInterface {

    public static final Map<Integer, Card> cardsList = loadModelCards();
    public static final Map<Integer, ClientCard> clientCardsList = loadClientCards();

    public static final Map<VirtualClient, Player> players = new HashMap<>();
    public static final Map<UUID, GameLobby> lobbiesAndGames = new HashMap<>();
    public static final Map<Player, ServerController> playersToControllers = new HashMap<>();

    public static final Map<VirtualClient, TimerTask> timeoutTasks = new HashMap<>();
    public static final long TIMEOUT_TASK_EXECUTION_AFTER = 30000;

    private static Map<Integer, Card> loadModelCards() {
        //TODO: map of maps?
        Map<Integer, Card> tmp = new HashMap<>();
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("resource_cards.json",
                        new TypeToken<ArrayList<ResourceCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("gold_cards.json",
                        new TypeToken<ArrayList<GoldCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("initial_cards.json",
                        new TypeToken<ArrayList<InitialCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));
        Objects.requireNonNull(JSONParser.deckFromJSONConstructor("objective_cards.json",
                        new TypeToken<ArrayList<ObjectiveCard>>(){}))
                .forEach((card) -> tmp.put(card.ID, card));

        return Collections.unmodifiableMap(tmp);
    }

    private static Map<Integer, ClientCard> loadClientCards() {
        return JSONParser.generateClientCardsFromJSON("client_cards.json")
                .stream().collect(Collectors.toMap((card) -> card.ID, (card) -> card));
    }

    public static ServerController getAssociatedController(Player target) {
        ServerController associatedController = playersToControllers.get(target);
        return associatedController == null ? ConnectionController.getInstance() : associatedController;
    }

    //Helper method to catch RemoteException (and eventually other ones) only one time
    public static void requestToClient(VirtualClient client, ClientCommand command) {
        try {
            client.requestToClient(command);
        } catch (IOException e) {
            //If communication is closed, the target has lost an update, so in case he reconnects, its game is inconsistent, we must send the update,
            //so the TimeoutTask routine has to be instantly executed.
            timeoutTasks.get(client).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean hasNoPlayer(VirtualClient client) {
        if (!players.containsKey(client)) {
            requestToClient(
                    client,
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Unregistered client")
                    )
            );
            return true;
        }
        return false;
    }

    protected void renewTimeoutTimerTask(VirtualClient target) {
        Timer timer = new Timer(true);
        TimerTask timeoutTask = new TimerTask() {
            @Override
            public void run() {
                Player thisPlayer = players.get(target);
                if (playersToControllers.containsKey(thisPlayer)) {
                    ServerController thisController = playersToControllers.get(thisPlayer);
                    if (thisController instanceof GameController)
                        leaveGame(target);
                    else
                        leaveLobby(target, true);
                }
            }
        };
        timer.schedule(timeoutTask, TIMEOUT_TASK_EXECUTION_AFTER);

        timeoutTasks.put(target, timeoutTask);
    }

    public void keepAlive(VirtualClient sender) {
        if (hasNoPlayer(sender)) return;

        if (timeoutTasks.containsKey(sender)) {
            timeoutTasks.get(sender).cancel();
            timeoutTasks.remove(sender);
            renewTimeoutTimerTask(sender);
        }
        System.out.println("[CLIENT]: keepAlive command received from " + sender + ". Resetting timeout");
    }

    public void disconnectionRoutine(VirtualClient target){
        System.out.println("[SERVER] Removing the entry of " + target + " since it didn't send any keepAlive in " + TIMEOUT_TASK_EXECUTION_AFTER/1000
                + " seconds or the game has sent an update and its state is inconsistent.");
        timeoutTasks.get(target).cancel();
        timeoutTasks.remove(target);
    }

    protected void generatePlayer(VirtualClient sender, String nickname) {
    }

    //FIXME: maybe now VirtualClients are no longer needed in here?
    public void createPlayer(VirtualClient sender, String nickname) {
        if (players.containsKey(sender)) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Client already registered")
                    )
            );
            return;
        }

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();

        if (selectedPlayer.isPresent()) {
            System.out.println("[SERVER]: sending an Exception while trying to log in to " + sender);
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided nickname is already taken")
                    )
            );
            return;
        }

        System.out.println("[CLIENT]: CreatePlayerCommand received and being executed");
        Optional<Player> target = playersToControllers.keySet().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny();
        target.ifPresent((player) -> players.put(sender, player));
        getAssociatedController(target.orElse(null)).generatePlayer(sender, nickname);

        //Creating the timeoutRoutine that will be started in case the client doesn't send a keepAliveCommand in the 60 seconds span.
        renewTimeoutTimerTask(sender);
    }

    public void setNickname(VirtualClient sender, String nickname) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    public void createLobby(VirtualClient sender, int maxPlayers) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    public void joinLobby(VirtualClient sender, UUID lobbyUUID) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while in a lobby or in a game!"))
        );
    }

    public void pickColor(VirtualClient sender, Color color) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a lobby!"))
        );
    }

    public void leaveLobby(VirtualClient sender, boolean isInactive) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a lobby!"))
        );
    }

    public void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void pickObjective(VirtualClient sender, int cardID) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void drawFromDeck(VirtualClient sender, String deck) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void drawFromVisibleCards(VirtualClient sender, String deck, int position) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void leaveGame(VirtualClient sender) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void broadcastMessage(VirtualClient sender, String message) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }

    public void directMessage(VirtualClient sender, String receiverNickname, String message) {
        requestToClient(
                sender,
                new ThrowExceptionCommand(new ForbiddenActionException("Cannot execute action while not in a game!"))
        );
    }
}