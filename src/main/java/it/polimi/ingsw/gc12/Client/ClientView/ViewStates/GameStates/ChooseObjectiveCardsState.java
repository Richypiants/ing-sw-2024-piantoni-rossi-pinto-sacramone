package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PickObjectiveCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;

import java.util.ArrayList;
import java.util.List;

public class ChooseObjectiveCardsState extends GameScreenState {

    public ArrayList<ClientCard> objectivesSelection = new ArrayList<>();

    public ChooseObjectiveCardsState() {
        TUICommands = List.of("'pickObjective <selection>' [1][2] per selezionare il proprio obiettivo segreto",
                "'broadcastMessage <message>' per inviare un messaggio in gioco (max 200 chars)",
                "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco (max 200 chars)"
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
            throw new IllegalArgumentException("nessuna carta obiettivo presente alla posizione specificata");
        }

        CLIENT.requestToServer(new PickObjectiveCommand(card.ID));
    }

    @Override
    public void transition() {
        currentState = new PlayerTurnPlayState();
    }
}
