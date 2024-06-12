package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Network.Client.Client;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.List;
import java.util.UUID;

public abstract class ViewState {

    public List<String> TUICommands = null;

    public final static ClientController CLIENT_CONTROLLER = ClientController.getInstance();

    public final static Client CLIENT = Client.getClientInstance();

    protected static ViewState currentState;

    protected static View selectedView;

    public static void setView(View view){
        selectedView = view;
    }

    public static ViewState getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(ViewState newState) {
        currentState = newState;
    }

    public void keyPressed() {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void setNickname(String nickname){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void createLobby(int maxPlayers){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void joinLobby(UUID lobbyUUID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void selectColor(Color color) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void leaveLobby(){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void drawFromDeck(String deck) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void drawFromVisibleCards(String deck, int position) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void showField(int opponentID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    //FIXME: TUI-only command, should be moved from here...?
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void pickObjective(int cardID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void broadcastMessage(String message) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void directMessage(String receiverNickname, String message) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command!"));
    }

    public void connect(String serverIPAddress, String communicationTechnology, String nickname) {
    }

    public void updateNickname() {
    }

    public void showPlacedCard(String nickname) {
    }

    public void showReceivedChatMessage(String message) {
    }

    public void toLobbies() {
    }

    public void printError(Throwable error) {
        selectedView.printError(error);
    }

    public void quit() {
        try {
            CLIENT.serverConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CLIENT.serverConnection = null;
        CLIENT.session = null;
        CLIENT.keepAlive.interrupt();
        CLIENT.keepAlive = null;
        CLIENT_CONTROLLER.VIEWMODEL.setOwnNickname("");
        CLIENT_CONTROLLER.VIEWMODEL.leaveRoom();

        currentState = new TitleScreenState();
        currentState.executeState();
    }

    public abstract void executeState();
}
