package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;

import java.util.UUID;

public interface VirtualServer {

    void createPlayer(String nickname);

    void setNickname(String nickname);

    void keepAlive();

    void createLobby(int maxPlayers);

    void joinLobby(UUID lobbyUUID);

    void leaveLobby();

    void pickObjective(ClientCard card) throws ForbiddenActionException, InvalidCardTypeException, AlreadySetCardException;

    void placeCard(GenericPair<Integer, Integer> position, ClientCard card, Side side) throws UnexpectedPlayerException, ForbiddenActionException, InvalidCardTypeException;

    void drawFromDeck(String deck) throws UnexpectedPlayerException, ForbiddenActionException;

    void drawFromVisibleCards(String deck, int position) throws UnexpectedPlayerException, ForbiddenActionException, InvalidDeckPositionException, UnknownStringException;

    void leaveGame();

    void directMessage(ClientPlayer receiver, String message);

    void broadcastMessage(String message);
}
