package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PickObjectiveCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;

import java.util.ArrayList;
import java.util.List;

public class ChooseObjectiveCardsState extends GameScreenState {

    public ArrayList<ClientCard> objectivesSelection = new ArrayList<>();

    public ChooseObjectiveCardsState() {
        TUICommands = List.of(
                "'pickObjective <selection>' [1][2] to select your personal objective",
                "'showField <playerID>' to show the player's field",
                "'broadcastMessage <message>' to send a message to all players (max 200 chars)",
                "'directMessage <recipient> <message> to send a private message @recipient (max 200 chars)"
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
        //FIXME: ...cannot happen? If this is restored it means I had disconnected and the server has already
        // played for me...
    }

    @Override
    public void pickObjective(int selection){
        ClientCard card;
        try {
            card = objectivesSelection.get(selection - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("There's no such objective card at that position.");
        }

        CLIENT.requestToServer(new PickObjectiveCommand(card.ID));
    }

    @Override
    public void transition() {
        currentState = new PlayerTurnPlayState();
    }
}
