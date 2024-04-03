package it.polimi.ingsw.gc12.ServerController;

public class RMIServerStubDeprecated /*implements RMIVirtualServer*/ {

    /*
    public static final Map<RMIVirtualClient, Player> RMIPlayers = new HashMap<>();

    private RMIServerStubDeprecated() {
        try {
            UnicastRemoteObject.exportObject(this, 5001);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private Player getPlayerFromVirtualClient(RMIVirtualClient client) {
        return RMIPlayers.get(client);
    }

    @Override
    public void createPlayer(RMIVirtualClient target, String nickname) throws RemoteException {
        Player result = Controller.createPlayer(nickname);
        //FIXME: se ho successo nel creare il player:
        RMIPlayers.put(target, result);
    }

    @Override
    public void setNickname(RMIVirtualClient target, String nickname) throws RemoteException {
        Controller.setNickname(getPlayerFromVirtualClient(target), nickname);
    }

    @Override
    public void keepAlive(RMIVirtualClient target) throws RemoteException {
        Controller.keepAlive(getPlayerFromVirtualClient(target));
    }

    @Override
    public void createLobby(RMIVirtualClient target, int maxPlayers) throws RemoteException {
        Controller.createLobby(getPlayerFromVirtualClient(target), maxPlayers);
    }

    @Override
    public void joinLobby(RMIVirtualClient target, UUID lobbyUUID) throws RemoteException {
        Controller.joinLobby(getPlayerFromVirtualClient(target), lobbyUUID);
    }

    @Override
    public void leaveLobby(RMIVirtualClient target) throws RemoteException {
        Controller.leaveLobby(getPlayerFromVirtualClient(target));
    }

    @Override
    public void placeInitialCard(RMIVirtualClient target, Side side) throws RemoteException, ForbiddenActionException {
        Controller.placeInitialCard((InGamePlayer) getPlayerFromVirtualClient(target), side);
    }

    @Override
    public void pickObjective(RMIVirtualClient target, ClientCard card) throws RemoteException, ForbiddenActionException, AlreadySetCardException {
        Controller.pickObjective((InGamePlayer) getPlayerFromVirtualClient(target), card);
    }

    @Override
    public void placeCard(RMIVirtualClient target, GenericPair<Integer, Integer> position, ClientCard card, Side side) throws RemoteException, UnexpectedPlayerException, ForbiddenActionException {
        Controller.placeCard((InGamePlayer) getPlayerFromVirtualClient(target), position, card, side);
    }

    @Override
    public void drawFromDeck(RMIVirtualClient target, String deck) throws RemoteException, UnexpectedPlayerException, ForbiddenActionException {
        Controller.drawFromDeck((InGamePlayer) getPlayerFromVirtualClient(target), deck);
    }

    @Override
    public void drawFromVisibleCards(RMIVirtualClient target, String deck, int position) throws RemoteException, UnexpectedPlayerException, ForbiddenActionException, InvalidPositionException, UnknownStringException {
        Controller.drawFromVisibleCards((InGamePlayer) getPlayerFromVirtualClient(target), deck,
                position);
    }

    @Override
    public void leaveGame(RMIVirtualClient target) throws RemoteException {
        Controller.leaveGame((InGamePlayer) getPlayerFromVirtualClient(target));
    }

    @Override
    public void directMessage(RMIVirtualClient target, ClientPlayer receiver, String message) throws RemoteException {
        Controller.directMessage((InGamePlayer) getPlayerFromVirtualClient(target), receiver, message);
    }

    @Override
    public void broadcastMessage(RMIVirtualClient target, String message) throws RemoteException {
        Controller.broadcastMessage((InGamePlayer) getPlayerFromVirtualClient(target), message);
    }*/
}
