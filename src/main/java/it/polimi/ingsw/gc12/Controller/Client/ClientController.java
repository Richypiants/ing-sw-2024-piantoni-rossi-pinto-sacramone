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

public class ClientController implements ClientControllerInterface {

    private static final ClientController SINGLETON_INSTANCE = new ClientController();

    public final ViewModel VIEWMODEL;

    public final Client CLIENT;

    public final ErrorLogger ERROR_LOGGER;

    private ClientController() {
        VIEWMODEL = new ViewModel();
        CLIENT = Client.getClientInstance();
        ERROR_LOGGER = new ErrorLogger();
    }

    public static ClientController getInstance() {
        return SINGLETON_INSTANCE;
    }

    public void throwException(Exception e) {
        ViewState.printError(e);
    }

    public void keepAlive() {
        synchronized (CLIENT.DISCONNECTED_LOCK) {
            CLIENT.disconnected = false;
            CLIENT.DISCONNECTED_LOCK.notifyAll();
        }
    }

    //TODO: check that without synchronized everything still works fine
    public void setNickname(String nickname) {
        VIEWMODEL.setOwnNickname(nickname);
        ViewState.getCurrentState().updateNickname();
    }

    public void setLobbies(Map<UUID, Lobby> lobbies) {
        VIEWMODEL.setLobbies(lobbies);
        if (!(ViewState.getCurrentState() instanceof LeaderboardScreenState)) {
            LobbiesScreenState newState = new LobbiesScreenState();
            ViewState.setCurrentState(newState);
            newState.executeState();
        }
    }

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

    public void startGame(ClientGame gameDTO) {
        VIEWMODEL.joinRoom(gameDTO);

        ViewState.setCurrentState(new ChooseInitialCardsState());
    }

    public void restoreGame(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD) {
        for(var playerEntry : PLAYERS_FIELD.entrySet()) {
            var clientPlayerInstance = gameDTO.getPlayers().stream()
                    .filter( (player) -> player.getNickname().equals(playerEntry.getKey())).findFirst().orElseThrow();

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

    public void receiveObjectiveChoice(List<Integer> cardIDs) {
        ChooseObjectiveCardsState newState = new ChooseObjectiveCardsState();
        ViewState.setCurrentState(newState);

        for (var cardID : cardIDs)
            newState.objectivesSelection.add(ViewModel.CARDS_LIST.get(cardID));

        newState.executeState();
    }

    public void confirmObjectiveChoice(int cardID) {
        VIEWMODEL.getCurrentGame().setOwnObjective(ViewModel.CARDS_LIST.get(cardID));
        ViewState.getCurrentState().executeState();
    }

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

    public void receiveCard(int cardID) {
        VIEWMODEL.getCurrentGame().addCardToHand(ViewModel.CARDS_LIST.get(cardID));

        ViewState.getCurrentState().executeState();
    }

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

    //FIXME: implementare visivamente numero di turni alla fine
    public void transition(int round, int currentPlayerIndex, int turnsLeftUntilGameEnds) {
        if(round != 0)
            VIEWMODEL.getCurrentGame().setCurrentRound(round);

        VIEWMODEL.getCurrentGame().setCurrentPlayerIndex(currentPlayerIndex);

        ((GameScreenState) ViewState.getCurrentState()).transition();
        ViewState.getCurrentState().executeState();
    }

    public void pauseGame() {
        AwaitingReconnectionState newState = new AwaitingReconnectionState(ViewState.getCurrentState());
        ViewState.setCurrentState(newState);
        VIEWMODEL.getCurrentGame().setCurrentPlayerIndex(-1);

        newState.executeState();
    }

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
    }

    public void endGame(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections) {
        LeaderboardScreenState newState = new LeaderboardScreenState(pointsStats, gameEndedDueToDisconnections);
        ViewState.setCurrentState(newState);
        newState.executeState();
    }

    public void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate) {
        ViewState.getCurrentState().showReceivedChatMessage(((isPrivate) ? "<Private> " : "") + "[" + senderNickname + "] " + chatMessage);
    }

    public boolean isThisClientTurn(){
        ClientGame game = VIEWMODEL.getCurrentGame();
        return game.getPlayers()
                .get(game.getCurrentPlayerIndex())
                .getNickname()
                .equals(game.getThisPlayer().getNickname());
    }
}
