package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.util.UUID;

public interface ServerControllerInterface extends ControllerInterface {

    void createPlayer(VirtualClient sender, String nickname) throws Exception;

    void setNickname(VirtualClient sender, String nickname) throws Exception;

    void keepAlive(VirtualClient sender) throws Exception;

    void createLobby(VirtualClient sender, int maxPlayers) throws Exception;

    void joinLobby(VirtualClient sender, UUID lobbyUUID) throws Exception;

    void leaveLobby(VirtualClient sender) throws Exception;

    void pickObjective(VirtualClient sender, int cardID) throws Exception;

    void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) throws Exception;

    void drawFromDeck(VirtualClient sender, String deck) throws Exception;

    void drawFromVisibleCards(VirtualClient sender, String deck, int position) throws Exception;

    void leaveGame(VirtualClient sender) throws Exception;

    void directMessage(VirtualClient sender, String receiverNickname, String message) throws Exception;

    void broadcastMessage(VirtualClient sender, String message) throws Exception;
}
