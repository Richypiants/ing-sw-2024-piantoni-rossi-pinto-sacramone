package it.polimi.ingsw.gc12.Client.ClientController;

import it.polimi.ingsw.gc12.Client.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Client.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.UUID;

public class Controller {

    private static final Controller SINGLETON_CONTROLLER = new Controller();

    private static ServerStub server;

    private Controller() {

    }

    public static Controller getInstance() {
        return SINGLETON_CONTROLLER;
    }

    public void createPlayer(String nickname) {
        server.createPlayer(nickname);
    }

    public void setNickname(String nickname) {
        server.setNickname(nickname);
    }

    //Si può fare in ogni stato: gestire che non possa essere il primo messaggio
    public void keepAlive() {
        server.keepAlive();
    }

    public void createLobby(int maxPlayers) {
        server.createLobby(maxPlayers);
    }

    public void joinLobby(UUID lobbyUUID) {
        server.joinLobby(lobbyUUID);
    }

    public void leaveLobby() {
        server.leaveLobby();
    }

    public void placeInitialCard(Side side) throws ForbiddenActionException {
        server.placeInitialCard(side);
    }

    public void pickObjective(ClientCard card) throws ForbiddenActionException,
            AlreadySetCardException {
        server.pickObjective(card);
    }

    public void placeCard(GenericPair<Integer, Integer> position, ClientCard card, Side side)
            throws UnexpectedPlayerException, ForbiddenActionException {
        server.placeCard(position, card, side);
    }

    public void drawFromDeck(String deck) throws UnexpectedPlayerException,
            ForbiddenActionException {
        server.drawFromDeck(deck);
    }

    public void drawFromVisibleCards(String deck, int position) throws UnexpectedPlayerException,
            ForbiddenActionException, InvalidPositionException, UnknownStringException {
        server.drawFromVisibleCards(deck, position);
    }

    public void leaveGame() {
        server.leaveGame();
    }

    public void directMessage(ClientPlayer receiver, String message) {
        server.directMessage(receiver, message);
    }

    public void broadcastMessage(String message) {
        server.broadcastMessage(message);
    }
}
