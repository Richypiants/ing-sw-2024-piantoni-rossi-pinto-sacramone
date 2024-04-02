package it.polimi.ingsw.gc12.ServerController;

import it.polimi.ingsw.gc12.Client.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Client.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface RMIVirtualServer extends Remote {

    public void createPlayer(RMIVirtualClient target, String nickname) throws RemoteException;

    public void setNickname(RMIVirtualClient target, String nickname) throws RemoteException;

    public void keepAlive(RMIVirtualClient target) throws RemoteException;

    public void createLobby(RMIVirtualClient target, int maxPlayers) throws RemoteException;

    public void joinLobby(RMIVirtualClient target, UUID lobbyUUID) throws RemoteException;

    public void leaveLobby(RMIVirtualClient target) throws RemoteException;

    public void placeInitialCard(RMIVirtualClient target, Side side) throws RemoteException, ForbiddenActionException;

    public void pickObjective(RMIVirtualClient target, ClientCard card) throws RemoteException, ForbiddenActionException, AlreadySetCardException;

    public void placeCard(RMIVirtualClient target, GenericPair<Integer, Integer> position, ClientCard card,
                          Side side) throws RemoteException, UnexpectedPlayerException, ForbiddenActionException;

    public void drawFromDeck(RMIVirtualClient target, String deck) throws RemoteException, UnexpectedPlayerException, ForbiddenActionException;

    public void drawFromVisibleCards(RMIVirtualClient target, String deck, int position) throws RemoteException, UnexpectedPlayerException, ForbiddenActionException, InvalidPositionException, UnknownStringException;

    public void leaveGame(RMIVirtualClient target) throws RemoteException;

    public void directMessage(RMIVirtualClient target, ClientPlayer receiver, String message) throws RemoteException;

    public void broadcastMessage(RMIVirtualClient target, String message) throws RemoteException;
}
