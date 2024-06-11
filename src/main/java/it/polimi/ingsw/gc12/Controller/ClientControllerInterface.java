package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.*;

public interface ClientControllerInterface extends ControllerInterface {

    void throwException(Exception e);

    void setNickname(String nickname);

    void restoreGame(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD);

    void setLobbies(Map<UUID, Lobby> lobbies);

    void updateLobby(Lobby lobby);

    void startGame(ClientGame gameDTO);

    void confirmObjectiveChoice(int cardID);

    void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide,
                   EnumMap<Resource, Integer> ownedResources, List<GenericPair<Integer, Integer>> openCorners,
                   int points);

    void receiveObjectiveChoice(List<Integer> cardIDs);

    void receiveCard(List<Integer> cardIDs);

    void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements);

    void toggleActive(String nickname);

    void transition(int round, int currentPlayerIndex);

    void pauseGame();

    void endGame(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections);

    void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate);
}
