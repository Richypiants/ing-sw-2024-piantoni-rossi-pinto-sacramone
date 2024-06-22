package it.polimi.ingsw.gc12.View.Client.ViewStates;

/**
 * Represents the title screen state of the client-side view.
 * Extends {@link ViewState}.
 */
public class TitleScreenState extends ViewState {

    /**
     * Constructs a new TitleScreenState instance.
     */
    public TitleScreenState() {}

    /**
     * Executes the behavior of the title screen state by displaying the title screen on the selected view.
     */
    @Override
    public void executeState() {
        selectedView.titleScreen();
    }

    /**
     * Handles the key press event in the title screen state by transitioning to the connection setup state.
     * Sets {@link #currentState} to a new {@link ConnectionSetupState} instance and executes it.
     */
    @Override
    public void keyPressed() {
        currentState = new ConnectionSetupState();
        currentState.executeState();
    }

    /**
     * Returns a string representation of the title screen state.
     *
     * @return The string "title screen".
     */
    @Override
    public String toString() {
        return "title screen";
    }
}
