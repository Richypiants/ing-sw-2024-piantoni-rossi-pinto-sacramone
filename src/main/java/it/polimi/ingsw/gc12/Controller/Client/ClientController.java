package it.polimi.ingsw.gc12.Controller.Client;

import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.ClientModel.ViewModel;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Network.Client.Client;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.ErrorLogger;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates.*;
import it.polimi.ingsw.gc12.View.Client.ViewStates.LeaderboardScreenState;
import it.polimi.ingsw.gc12.View.Client.ViewStates.LobbiesScreenState;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

import java.util.*;

/**
 * The ClientController class manages client-side operations and interactions with the game server.
 * It handles network communication, updates client-side data (ViewModel), manages game states,
 * and interacts with the user interface (ViewState).
 */
public class ClientController implements ClientControllerInterface {

    /** The Singleton instance of ClientController. */
    private static final ClientController SINGLETON_INSTANCE = new ClientController();

    /**
     * The ViewModel instance that holds client-side data related to the game state,
     * lobby information, and player details.
     */
    public final ViewModel VIEWMODEL;

    /**
     * The Client instance responsible for network communication with the game server.
     */
    public final Client CLIENT;

    /**
     * The ErrorLogger instance for logging errors and exceptions that occur on the client side.
     */
    public final ErrorLogger ERROR_LOGGER;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private ClientController() {
        VIEWMODEL = new ViewModel();
        CLIENT = Client.getClientInstance();
        ERROR_LOGGER = new ErrorLogger();
    }

    /**
     * Retrieves the Singleton instance of ClientController.
     *
     * @return The Singleton instance of ClientController.
     */
    public static ClientController getInstance() {
        return SINGLETON_INSTANCE;
    }

    /**
     * Handles exceptions thrown during client operation by printing them to the current view state.
     *
     * @param e The exception to handle.
     */
    public void throwException(Exception e) {
        ViewState.printError(e);
    }

    /**
     * Sends a keep-alive command to the server to indicate that the client is still active.
     */
    public void keepAlive() {
        synchronized (CLIENT.DISCONNECTED_LOCK) {
            CLIENT.disconnected = false;
            CLIENT.DISCONNECTED_LOCK.notifyAll();
        }
    }

    /**
     * Sets the nickname of the client and updates the ViewModel and refreshes the view state.
     *
     * @param nickname The nickname to set for the client.
     */
    //TODO: check that without synchronized everything still works fine
    public void setNickname(String nickname) {
        synchronized (CLIENT.DISCONNECTED_LOCK) {
            CLIENT.disconnected = false;
            CLIENT.DISCONNECTED_LOCK.notifyAll();
        }
        VIEWMODEL.setOwnNickname(nickname);
        ViewState.getCurrentState().updateNickname();
    }

    /**
     * Sets the lobbies received from the server in the ViewModel and updates the current view state.
     *
     * @param lobbies The map of lobbies received from the server.
     */
    public void setLobbies(Map<UUID, Lobby> lobbies) {
        VIEWMODEL.setLobbies(lobbies);
        if (!(ViewState.getCurrentState() instanceof LeaderboardScreenState)) {
            VIEWMODEL.leaveRoom();

            LobbiesScreenState newState = new LobbiesScreenState();
            ViewState.setCurrentState(newState);
            newState.executeState();
        }
    }

    /**
     * Updates the lobby in the ViewModel and updates the current view state.
     * When a lobby has no players, it gets deleted, if contains this client's nickname, the client joins it on the ViewModel,
     * if the client isn't in the received lobby anymore, and it was in previously, leaves it and update the ViewModel accordingly.
     *
     * @param lobby The updated lobby received from the server.
     */
    public void updateLobby(Lobby lobby) {
        //The received lobbies with a playersNumber equal to zero or below are removed from the ClientModel
        if(lobby.getPlayersNumber() <= 0)
            VIEWMODEL.removeLobby(lobby.getRoomUUID());
        else
            VIEWMODEL.putLobby(lobby.getRoomUUID(), lobby);

        if (lobby.getPlayers().stream().anyMatch((player) -> player.getNickname().equals(VIEWMODEL.getOwnNickname()))) {
            VIEWMODEL.joinRoom(lobby);
        }
        //If I was in the lobby received but I am no longer in it (noneMatch()), then leave the lobby also on the ViewModel
        else if (lobby.getRoomUUID().equals(VIEWMODEL.getCurrentRoomUUID())) {
            VIEWMODEL.leaveRoom();
        }

        if (!(ViewState.getCurrentState() instanceof LeaderboardScreenState)) {
            LobbiesScreenState newState = new LobbiesScreenState();
            ViewState.setCurrentState(newState);
            newState.executeState();
        }
    }

    /**
     * Initiates the game on the client side with the provided game data received from the server.
     *
     * @param gameDTO The initial game data received from the server.
     */
    public void startGame(ClientGame gameDTO) {
        VIEWMODEL.joinRoom(gameDTO);

        ViewState.setCurrentState(new ChooseInitialCardsState());
    }

    /**
     * Restores the game state on the client side after reconnecting, using the provided game data.
     *
     * @param gameDTO         The game data received from the server.
     * @param currentState    The state of the game when the client disconnected.
     * @param PLAYERS_FIELD   The field state of players when the client disconnected.
     */
    public void restoreGame(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD) {
        for(var playerEntry : PLAYERS_FIELD.entrySet()) {
            var clientPlayerInstance = gameDTO.getPlayers().stream()
                    .filter((player) -> player.getNickname().equals(playerEntry.getKey())).findFirst().orElseThrow();

            for (var fieldEntry : PLAYERS_FIELD.get(playerEntry.getKey()).sequencedEntrySet())
                clientPlayerInstance.placeCard(fieldEntry.getKey(), ViewModel.CARDS_LIST.get(fieldEntry.getValue().getX()), fieldEntry.getValue().getY());
        }
        VIEWMODEL.joinRoom(gameDTO);

        GameScreenState restoredGameState = switch (currentState) {
            case "initialState" -> new ChooseInitialCardsState();
            case "objectiveState" -> new ChooseObjectiveCardsState();
            case "playState" -> new PlayerTurnPlayState();
            case "drawState" -> new PlayerTurnDrawState();
            default -> throw new IllegalArgumentException("Invalid restore state received: " + currentState);
        };

        ViewState.setCurrentState(restoredGameState);
        restoredGameState.restoreScreenState();
    }

    /**
     * Receives the chosen objective cards from the server and updates the client state accordingly.
     *
     * @param cardIDs The IDs of the objective cards chosen by the client.
     */
    public void receiveObjectiveChoice(List<Integer> cardIDs) {
        ChooseObjectiveCardsState newState = new ChooseObjectiveCardsState();
        ViewState.setCurrentState(newState);

        for (var cardID : cardIDs)
            newState.objectivesSelection.add(ViewModel.CARDS_LIST.get(cardID));

        newState.executeState();
    }

    /**
     * Confirms the chosen objective card ID and sets it in the client's game state.
     *
     * @param cardID The ID of the objective card chosen by the client.
     */
    public void confirmObjectiveChoice(int cardID) {
        VIEWMODEL.getCurrentGame().setOwnObjective(ViewModel.CARDS_LIST.get(cardID));
        ViewState.getCurrentState().executeState();
    }

    /**
     * Executes the placement of a card on the game board and updates the client's game state.
     *
     * @param nickname       The nickname of the player who placed the card.
     * @param coordinates    The coordinates where the card was placed.
     * @param cardID         The ID of the card placed.
     * @param playedSide     The side of the card that was played (top or bottom).
     * @param ownedResources The resources owned by the player after placing the card.
     * @param openCorners    The open corners on the player's tableau after placing the card.
     * @param points         The points scored by the player after placing the card.
     */
    public void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID,
                          Side playedSide, EnumMap<Resource, Integer> ownedResources,
                          List<GenericPair<Integer, Integer>> openCorners, int points) {
        ClientPlayer thisPlayer = VIEWMODEL.getCurrentGame().getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow();

        thisPlayer.placeCard(coordinates, ViewModel.CARDS_LIST.get(cardID), playedSide);
        if (nickname.equals(VIEWMODEL.getOwnNickname()))
            VIEWMODEL.getCurrentGame().removeCardFromHand(ViewModel.CARDS_LIST.get(cardID));
        thisPlayer.setOwnedResources(ownedResources);
        thisPlayer.setOpenCorners(openCorners);
        thisPlayer.setPoints(points);

        ViewState.getCurrentState().showPlacedCard(nickname);
    }

    /**
     * Receives a card from the server and adds it to the client's hand.
     *
     * @param cardID The ID of the card received.
     */
    public void receiveCard(int cardID) {
        VIEWMODEL.getCurrentGame().addCardToHand(ViewModel.CARDS_LIST.get(cardID));

        ViewState.getCurrentState().executeState();
    }

    /**
     * Updates the provided cards on the game board after a certain action and a replacement happens.
     *
     * @param cardPlacements The details of cards and their new positions on the game board.
     */
    public void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements) {
        for(var cardPlacement : cardPlacements) {
            ClientCard card = ViewModel.CARDS_LIST.get(cardPlacement.getX());
            switch (cardPlacement.getY().trim().toLowerCase()) {
                case "resource_deck" -> VIEWMODEL.getCurrentGame().setTopDeckResourceCard(card);
                case "gold_deck" -> VIEWMODEL.getCurrentGame().setTopDeckGoldCard(card);
                case "resource_visible" -> VIEWMODEL.getCurrentGame().setPlacedResources(card, cardPlacement.getZ());
                case "gold_visible" -> VIEWMODEL.getCurrentGame().setPlacedGold(card, cardPlacement.getZ());
                case "objective_visible" -> VIEWMODEL.getCurrentGame().setCommonObjectives(card, cardPlacement.getZ());
                default ->
                        throw new IllegalArgumentException("Invalid ReplaceCard string received: " + cardPlacement.getY());
            }
        }
    }

    /**
     * Initiates a transition in the game state based on the server's notification.
     *
     * @param round                   The current round number in the game.
     * @param currentPlayerIndex      The index of the current player in the game.
     * @param turnsLeftUntilGameEnds  The number of turns left until the game ends.
     */
    public void transition(int round, int currentPlayerIndex, int turnsLeftUntilGameEnds) {
        ClientGame thisGame = VIEWMODEL.getCurrentGame();
        if(round != 0)
            thisGame.setCurrentRound(round);

        if (turnsLeftUntilGameEnds != -1)
            thisGame.setTurnsLeftUntilGameEnds(turnsLeftUntilGameEnds);

        thisGame.setCurrentPlayerIndex(currentPlayerIndex);

        ((GameScreenState) ViewState.getCurrentState()).transition();
        ViewState.getCurrentState().executeState();
    }

    /**
     * Pauses the game and waits for reconnection after a disconnection.
     */
    public void pauseGame() {
        AwaitingReconnectionState newState = new AwaitingReconnectionState(ViewState.getCurrentState());
        ViewState.setCurrentState(newState);
        VIEWMODEL.getCurrentGame().setCurrentPlayerIndex(-1);

        newState.executeState();
    }

    /**
     * Toggles the active status of a player (connected or disconnected).
     *
     * @param nickname The nickname of the player whose active status is toggled.
     */
    public void toggleActive(String nickname) {
        ClientPlayer targetPlayer = VIEWMODEL.getCurrentGame().getPlayers().stream()
                .filter((player) -> player.getNickname().equals(nickname))
                .findAny()
                .orElseThrow();

        targetPlayer.toggleActive();
        addChatMessage(
                "SYSTEM",
                "Player " + nickname + " has " + (targetPlayer.isActive() ? "reconnected" : "disconnected"),
                false
        );

        ViewState.getCurrentState().executeState();
    }

    /**
     * Ends the game and displays the leaderboard with final scores.
     *
     * @param pointsStats                   The list of player nicknames, their total points, and points from objectives.
     * @param gameEndedDueToDisconnections  Flag indicating if the game ended due to disconnections.
     */
    public void endGame(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections) {
        LeaderboardScreenState newState = new LeaderboardScreenState(pointsStats, gameEndedDueToDisconnections);
        ViewState.setCurrentState(newState);
        newState.executeState();
    }

    /**
     * Adds a chat message to the current view state for display.
     *
     * @param senderNickname  The nickname of the sender of the chat message.
     * @param chatMessage     The content of the chat message.
     * @param isPrivate       Flag indicating if the message is private.
     */
    public void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate) {
        ViewState.getCurrentState().showReceivedChatMessage(((isPrivate) ? "<Private> " : "") + "[" + senderNickname + "] " + chatMessage);
    }

    /**
     * Checks if it's currently the client's turn in the game.
     *
     * @return {@code True} if it's the client's turn, {@code false} otherwise.
     */
    public boolean isThisClientTurn(){
        ClientGame game = VIEWMODEL.getCurrentGame();
        return game.getPlayers()
                .get(game.getCurrentPlayerIndex())
                .getNickname()
                .equals(game.getThisPlayer().getNickname());
    }
}
