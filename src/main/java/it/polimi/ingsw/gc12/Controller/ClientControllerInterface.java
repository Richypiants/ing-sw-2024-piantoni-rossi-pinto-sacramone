package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ClientControllerInterface extends ControllerInterface {

    void throwException(Exception e) throws Exception;

    void keepAlive();

    void setNickname(String nickname);

    void restoreGame(ClientGame gameDTO);

    void setLobbies(Map<UUID, GameLobby> lobbies);

    void updateLobby(UUID lobbyUUID, GameLobby lobby);

    void startGame(UUID lobbyUUID, ClientGame gameDTO);

    void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide,
                   EnumMap<Resource, Integer> ownedResources, List<GenericPair<Integer, Integer>> openCorners,
                   int points);

    void receiveCard(List<Integer> cardIDs);

    void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements);

    void toggleActive(String nickname);

    void endGame(List<Triplet<String, Integer, Integer>> pointsStats);

    void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate);
}
