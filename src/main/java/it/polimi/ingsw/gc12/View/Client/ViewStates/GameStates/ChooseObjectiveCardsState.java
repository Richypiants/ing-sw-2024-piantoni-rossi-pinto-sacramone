package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.PickObjectiveCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;

import java.util.ArrayList;
import java.util.List;

public class ChooseObjectiveCardsState extends GameScreenState {

    public ArrayList<ClientCard> objectivesSelection = new ArrayList<>();

    public ChooseObjectiveCardsState() {
        TUICommands = List.of(
                "'[pickObjective | po] <selection>' [1 | 2] to select your personal objective",
                "'[showField | sf] <playerID>' to show the player's field",
                "'[broadcastMessage | bm] <message>' to send a message to all players (max 200 chars)",
                "'[directMessage | dm] <recipient> <message> to send a private message (max 200 chars)"
        );
    }

    @Override
    public void executeState() {
        selectedView.gameScreen();
        if (CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getOwnObjective() == null)
            selectedView.showObjectiveCardsChoice(objectivesSelection);
    }

    public void restoreScreenState(){
        selectedView.gameScreen();
        if (CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getOwnObjective() == null)
            selectedView.showObjectiveCardsChoice(objectivesSelection);
    }

    @Override
    public void pickObjective(int selection){
        ClientCard card;
        try {
            card = objectivesSelection.get(selection - 1);
        } catch (IndexOutOfBoundsException e) {
            selectedView.printError(new IllegalArgumentException("There's no such objective card at that position!"));
            return;
        }

        CLIENT.requestToServer(new PickObjectiveCommand(card.ID));
    }

    @Override
    public void transition() {
        currentState = new PlayerTurnPlayState();
    }

    @Override
    public String toString() {
        return "Objective Card picking phase";
    }
}
