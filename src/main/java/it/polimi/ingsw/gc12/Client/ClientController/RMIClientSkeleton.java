package it.polimi.ingsw.gc12.Client.ClientController;

import it.polimi.ingsw.gc12.Client.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Client.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.ServerController.RMIVirtualClient;
import it.polimi.ingsw.gc12.ServerController.RMIVirtualServer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

//TODO: singleton!
public class RMIClientSkeleton implements ServerStub, RMIVirtualClient {

    public RMIVirtualServer controller;

    public RMIClientSkeleton() {
        try {
            Registry registry = LocateRegistry.getRegistry("???", 5001);
            this.controller = (RMIVirtualServer) registry.lookup("codex_naturalis_rmi_controller");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createPlayer(String nickname) {
        try {
            controller.createPlayer(this, nickname);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNickname(String nickname) {
        try {
            controller.setNickname(this, nickname);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //Si può fare in ogni stato: gestire che non possa essere il primo messaggio
    @Override
    public void keepAlive() {
        try {
            controller.keepAlive(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createLobby(int maxPlayers) {
        try {
            controller.createLobby(this, maxPlayers);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinLobby(UUID lobbyUUID) {
        try {
            controller.joinLobby(this, lobbyUUID);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leaveLobby() {
        try {
            controller.leaveLobby(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeInitialCard(Side side) throws ForbiddenActionException {
        try {
            controller.placeInitialCard(this, side);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pickObjective(ClientCard card) throws ForbiddenActionException,
            AlreadySetCardException {
        try {
            controller.pickObjective(this, card);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> position, ClientCard card, Side side)
            throws UnexpectedPlayerException, ForbiddenActionException {
        try {
            controller.placeCard(this, position, card, side);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawFromDeck(String deck) throws UnexpectedPlayerException,
            ForbiddenActionException {
        try {
            controller.drawFromDeck(this, deck);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawFromVisibleCards(String deck, int position) throws UnexpectedPlayerException,
            ForbiddenActionException, InvalidPositionException, UnknownStringException {
        try {
            controller.drawFromVisibleCards(this, deck, position);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leaveGame() {
        try {
            controller.leaveGame(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void directMessage(ClientPlayer receiver, String message) {
        try {
            controller.directMessage(this, receiver, message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcastMessage(String message) {
        try {
            controller.broadcastMessage(this, message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
