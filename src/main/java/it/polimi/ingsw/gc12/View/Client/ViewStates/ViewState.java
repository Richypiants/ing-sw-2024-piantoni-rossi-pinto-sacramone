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

/**
 * Abstract base class representing a state in the client-side view.
 * Each subclass represents a specific state with restricted actions and behaviors.
 */
public abstract class ViewState {

    /** List of TUI commands available in this state. */
    public List<String> TUICommands = null;

    /** Singleton instance of the client controller. */
    public final static ClientController CLIENT_CONTROLLER = ClientController.getInstance();

    /** Singleton instance of the client. */
    public final static Client CLIENT = Client.getClientInstance();

    /** Current state of the view state machine. */
    protected static ViewState currentState;

    /** Selected view instance associated with this state. */
    protected static View selectedView;

    /**
     * Sets the current view for this state.
     *
     * @param view The view to set.
     */
    public static void setView(View view){
        selectedView = view;
    }

    /**
     * Retrieves the current state of the view state machine.
     *
     * @return The current ViewState instance.
     */
    public static ViewState getCurrentState() {
        synchronized (ViewState.class) {
            return currentState;
        }
    }

    /**
     * Sets the current state of the view state machine.
     *
     * @param newState The new state to set.
     */
    public static void setCurrentState(ViewState newState) {
        synchronized (ViewState.class) {
            currentState = newState;
        }
    }

    /**
     * Prints an error message on the selected view and logs it to the error logger of the client controller.
     *
     * @param error The error object or exception to be displayed.
     */
    public static void printError(Throwable error) {
        selectedView.printError(error);
        CLIENT_CONTROLLER.ERROR_LOGGER.log(error);
    }

    /**
     * Handles key press events in this state (default behavior is to throw a ForbiddenActionException).
     */
    public void keyPressed() {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to set the nickname (default behavior is to throw a ForbiddenActionException).
     *
     * @param nickname The nickname to set.
     */
    public void setNickname(String nickname){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to create a lobby with a specified maximum number of players (default behavior is to throw a ForbiddenActionException).
     *
     * @param maxPlayers The maximum number of players allowed in the lobby.
     */
    public void createLobby(int maxPlayers){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to join a lobby with a specified lobby UUID (default behavior is to throw a ForbiddenActionException).
     *
     * @param lobbyUUID The UUID of the lobby to join.
     */
    public void joinLobby(UUID lobbyUUID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to select a color (default behavior is to throw a ForbiddenActionException).
     *
     * @param color The color to select.
     */
    public void selectColor(Color color) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to leave the lobby (default behavior is to throw a ForbiddenActionException).
     */
    public void leaveLobby(){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to place a card on the game board (default behavior is to throw a ForbiddenActionException).
     *
     * @param coordinates     The coordinates on the board where the card will be placed.
     * @param inHandPosition  The position of the card in hand.
     * @param playedSide      The side on which the card will be played.
     */
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to draw a card from a specified deck (default behavior is to throw a ForbiddenActionException).
     *
     * @param deck The deck from which to draw the card.
     */
    public void drawFromDeck(String deck) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to draw a card from visible cards at a specified position (default behavior is to throw a ForbiddenActionException).
     *
     * @param deck     The deck from which to draw the card.
     * @param position The position of the card in the visible cards.
     */
    public void drawFromVisibleCards(String deck, int position) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Displays the game field of the opponent with a specified ID (default behavior is to throw a ForbiddenActionException).
     *
     * @param opponentID The ID of the opponent whose field to display.
     */
    public void showField(int opponentID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Moves the game field display with a specified center offset (default behavior is to throw a ForbiddenActionException).
     *
     * @param centerOffset The offset to move the center of the field display.
     */
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to pick an objective card with a specified ID (default behavior is to throw a ForbiddenActionException).
     *
     * @param cardID The ID of the objective card to pick.
     */
    public void pickObjective(int cardID){
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Attempts to broadcast a message to all clients (default behavior is to throw a ForbiddenActionException).
     *
     * @param message The message to broadcast.
     */
    public void broadcastMessage(String message) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Connects to a server with specified IP address, communication technology, and nickname.
     *
     * @param serverIPAddress        The IP address of the server to connect to.
     * @param communicationTechnology The communication technology to use (e.g., TCP, UDP).
     * @param nickname               The nickname to use in the session.
     */
    public void connect(String serverIPAddress, String communicationTechnology, String nickname) {
    }

    /**
     * Updates the nickname of this client.
     */
    public void updateNickname() {}

    /**
     * Displays the placed card of a player specified by its nickname.
     *
     * @param nickname The nickname of the player whose placed card to display.
     */
    public void showPlacedCard(String nickname) {
    }

    /**
     * Displays a received chat message.
     *
     * @param message The message received.
     */
    public void showReceivedChatMessage(String message) {}

    /**
     * Navigates to the lobbies screen when a game ends.
     */
    public void toLobbies() {}

    /**
     * Sends a direct message to a specified receiver nickname (default behavior is to throw a ForbiddenActionException).
     *
     * @param receiverNickname The nickname of the message receiver.
     * @param message          The message content.
     */
    public void directMessage(String receiverNickname, String message) {
        selectedView.printError(new ForbiddenActionException("Cannot execute this command in " + this + "!"));
    }

    /**
     * Resets the client and clears the view model, then transitions to the title screen state.
     */
    public void quit() {
        CLIENT.resetClient();
        CLIENT_CONTROLLER.VIEWMODEL.clearModel();

        currentState = new TitleScreenState();
        currentState.executeState();
    }

    /**
     * Executes the current state's behavior.
     * Each subclass implements its own specific behavior for this method.
     */
    public abstract void executeState();
}
