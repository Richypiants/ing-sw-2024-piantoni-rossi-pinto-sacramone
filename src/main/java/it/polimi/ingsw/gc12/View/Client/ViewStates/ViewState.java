package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Network.Client.Client;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.ForbiddenActionException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.View.Client.View;

import java.util.List;
import java.util.UUID;

public abstract class ViewState {

    //FIXME: maybe move these in GameScreenState? no, better to use this for lobbyScreen commands too
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

    public static void printError(Throwable error) {
        selectedView.printError(error);
        CLIENT_CONTROLLER.ERROR_LOGGER.log(error);
    }

    public void keyPressed() {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void setNickname(String nickname){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void createLobby(int maxPlayers){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void joinLobby(UUID lobbyUUID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void selectColor(Color color) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void leaveLobby(){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void drawFromDeck(String deck) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void drawFromVisibleCards(String deck, int position) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void showField(int opponentID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    //FIXME: TUI-only command, should be moved from here...?
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void pickObjective(int cardID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void broadcastMessage(String message) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
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

    public void directMessage(String receiverNickname, String message) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    public void quit() {
        CLIENT.resetClient();
        CLIENT_CONTROLLER.VIEWMODEL.clearModel();

        currentState = new TitleScreenState();
        currentState.executeState();
    }

    public abstract void executeState();
}
