package it.polimi.ingsw.gc12.Client.ClientController;

import it.polimi.ingsw.gc12.Client.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Client.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.UUID;

//TODO: singleton!
public class RMIClientSkeleton implements ClientController, RMIVirtualClient {

    public Map<String, RMIVirtualMethod> methods;

    public RMIClientSkeleton() {
        try {
            Registry registry = LocateRegistry.getRegistry("???", 5001);
            this.methods = ((RMIVirtualServer) registry.lookup("codex_naturalis_rmi_methods")).getMap();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createPlayer(String nickname) {
        try {
            methods.get("createPlayer").invokeWithArguments(this, nickname);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNickname(String nickname) {
        try {
            methods.get("setNickname").invokeWithArguments(this, nickname);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    //Si può fare in ogni stato: gestire che non possa essere il primo messaggio
    @Override
    public void keepAlive() {
        try {
            methods.get("keepAlive").invokeWithArguments(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createLobby(int maxPlayers) {
        try {
            methods.get("createLobby").invokeWithArguments(this, maxPlayers);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinLobby(UUID lobbyUUID) {
        try {
            methods.get("joinLobby").invokeWithArguments(this, lobbyUUID);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leaveLobby() {
        try {
            methods.get("leaveLobby").invokeWithArguments(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeInitialCard(Side side) throws ForbiddenActionException, InvalidCardTypeException {
        try {
            methods.get("placeInitialCard").invokeWithArguments(this, side);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pickObjective(ClientCard card) throws ForbiddenActionException, InvalidCardTypeException,
            AlreadySetCardException {
        try {
            methods.get("pickObjective").invokeWithArguments(this, card);
            ;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> position, ClientCard card, Side side)
            throws UnexpectedPlayerException, ForbiddenActionException, InvalidCardTypeException {
        try {
            methods.get("placeCard").invokeWithArguments(this, position, card, side);
            ;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawFromDeck(String deck) throws UnexpectedPlayerException,
            ForbiddenActionException {
        try {
            methods.get("drawFromDeck").invokeWithArguments(this, deck);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawFromVisibleCards(String deck, int position) throws UnexpectedPlayerException,
            ForbiddenActionException, InvalidPositionException, UnknownStringException {
        try {
            methods.get("drawFromVisibleCards").invokeWithArguments(this, deck, position);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leaveGame() {
        try {
            methods.get("leaveGame").invokeWithArguments(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void directMessage(ClientPlayer receiver, String message) {
        try {
            methods.get("directMessage").invokeWithArguments(this, receiver, message);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcastMessage(String message) {
        try {
            methods.get("broadcastMessage").invokeWithArguments(this, message);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
