package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.UUID;

public interface ServerControllerInterface extends ControllerInterface {

    void createPlayer(NetworkSession sender, String nickname);

    void setNickname(NetworkSession sender, String nickname);

    void keepAlive(NetworkSession sender);

    void createLobby(NetworkSession sender, int maxPlayers);

    void joinLobby(NetworkSession sender, UUID lobbyUUID);

    void pickColor(NetworkSession sender, Color color);

    void leaveLobby(NetworkSession sender, boolean isInactive);

    void pickObjective(NetworkSession sender, int cardID);

    void placeCard(NetworkSession sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide);

    void drawFromDeck(NetworkSession sender, String deck);

    void drawFromVisibleCards(NetworkSession sender, String deck, int position);

    void leaveGame(NetworkSession sender);

    void directMessage(NetworkSession sender, String receiverNickname, String message);

    void broadcastMessage(NetworkSession sender, String message);
}
