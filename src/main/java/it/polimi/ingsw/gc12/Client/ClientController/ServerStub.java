package it.polimi.ingsw.gc12.Client.ClientController;

import it.polimi.ingsw.gc12.Client.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Client.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.UUID;

public interface ServerStub {

    void createPlayer(String nickname);

    void setNickname(String nickname);

    void keepAlive();

    void createLobby(int maxPlayers);

    void joinLobby(UUID lobbyUUID);

    void leaveLobby();

    void placeInitialCard(Side side);

    void pickObjective(ClientCard card);

    void placeCard(GenericPair<Integer, Integer> position, ClientCard card, Side side);

    void drawFromDeck(String deck);

    void drawFromVisibleCards(String deck, int position);

    void leaveGame();

    void directMessage(ClientPlayer receiver, String message);

    void broadcastMessage(String message);
}
