package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.util.UUID;

public interface ServerControllerInterface extends ControllerInterface {

    void createPlayer(VirtualClient sender, String nickname);

    void setNickname(VirtualClient sender, String nickname);

    void keepAlive(VirtualClient sender);

    void createLobby(VirtualClient sender, int maxPlayers);

    void joinLobby(VirtualClient sender, UUID lobbyUUID);

    void leaveLobby(VirtualClient sender);

    void pickObjective(VirtualClient sender, int cardID);

    void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide);

    void drawFromDeck(VirtualClient sender, String deck);

    void drawFromVisibleCards(VirtualClient sender, String deck, int position);

    void leaveGame(VirtualClient sender);

    void directMessage(VirtualClient sender, String receiverNickname, String message);

    void broadcastMessage(VirtualClient sender, String message);
}
