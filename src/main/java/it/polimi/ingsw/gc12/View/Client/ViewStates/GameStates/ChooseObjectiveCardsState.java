package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.PickObjectiveCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the game client where the player needs to choose the personal objective card.
 * Extends {@link GameScreenState}.
 */
public class ChooseObjectiveCardsState extends GameScreenState {

    /**
     * List of objective cards available for selection.
     */
    public ArrayList<ClientCard> objectivesSelection = new ArrayList<>();

    /**
     * Constructs a ChooseObjectiveCardsState and initializes the TUI commands specific to this state.
     */
    public ChooseObjectiveCardsState() {
        TUICommands = List.of(
                "'[pickObjective | po] <selection>' [1 | 2] to select your personal objective",
                "'[showField | sf] <playerID>' to show the player's field",
                "'[broadcastMessage | bm] <message>' to send a message to all players (max 200 chars)",
                "'[directMessage | dm] <recipient> <message> to send a private message (max 200 chars)"
        );
    }

    /**
     * Executes the behavior of the choose objective cards state by displaying the game screen and showing the objective cards choice if necessary.
     */
    @Override
    public void executeState() {
        selectedView.gameScreen();
        if (CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getOwnObjective() == null)
            selectedView.showObjectiveCardsChoice(objectivesSelection);
    }

    /**
     * Restores the screen state by displaying the game screen and showing the objective cards choice if necessary.
     */
    public void restoreScreenState() {
        selectedView.gameScreen();
        if (CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getOwnObjective() == null)
            selectedView.showObjectiveCardsChoice(objectivesSelection);
    }

    /**
     * Picks an objective card based on the player's selection and sends the pick objective command to the server.
     *
     * @param selection The index of the objective card selected by the player (1-based index).
     */
    @Override
    public void pickObjective(int selection) {
        ClientCard card;
        try {
            card = objectivesSelection.get(selection - 1);
        } catch (IndexOutOfBoundsException e) {
            selectedView.printError(new IllegalArgumentException("There's no such objective card at that position!"));
            return;
        }

        CLIENT.requestToServer(new PickObjectiveCommand(card.ID));
    }

    /**
     * Handles the transition to the next state, setting the current state to {@link PlayerTurnPlayState}.
     */
    @Override
    public void transition() {
        currentState = new PlayerTurnPlayState();
    }

    /**
     * Returns a string representation of the choose objective cards state.
     *
     * @return The string "Objective Card picking phase".
     */
    @Override
    public String toString() {
        return "Objective Card picking phase";
    }
}
