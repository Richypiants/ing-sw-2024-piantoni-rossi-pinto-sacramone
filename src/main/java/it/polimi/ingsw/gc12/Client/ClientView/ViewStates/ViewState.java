package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.List;
import java.util.UUID;

public abstract class ViewState {

    public List<String> TUICommands = null;

    protected static View selectedView;
    protected static ViewState currentState;

    public static ViewState getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(ViewState currentState) {
        ViewState.currentState = currentState;
    }

    public static View getView() {
        return selectedView;
    }

    public static void setView(View view){
        selectedView = view;
    }

    public void keyPressed() {

    }

    public void setNickname(String nickname){
        //"throw Exception"
    }

    public void connect(String serverIPAddress, String communicationTechnology, String nickname) {

    }

    public void createLobby(int maxPlayers){

    }

    public void joinLobby(UUID lobbyUUID){

    }

    public void leaveLobby(){

    }

    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {

    }

    public void placeCard(int inHandPosition) {

    }

    public void drawFromDeck(String deck) {

    }

    public void drawFromVisibleCards(String deck, int position) {

    }

    public void showField(int opponentID){

    }

    public void pickObjective(int cardID){

    }

    public void updateNickname() {

    }

    public void broadcastMessage(String message) {

    }

    public void directMessage(String receiverNickname, String message) {

    }

    public void addChatMessage(String message) {

    }

    public void toLobbies() {
    }

    public void quit() {
        ClientController.getInstance().thisClient = null;
        try {
            ClientController.getInstance().serverConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ClientController.getInstance().serverConnection = null;
        ClientController.getInstance().keepAlive.interrupt();
        ClientController.getInstance().viewModel.setOwnNickname("");
        ClientController.getInstance().viewModel.leaveLobbyOrGame();
        ClientController.getInstance().viewState = new TitleScreenState();
        ClientController.getInstance().viewState.executeState();
    }

    public abstract void executeState();
}
