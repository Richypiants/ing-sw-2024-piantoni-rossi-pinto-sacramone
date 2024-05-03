package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PickObjectiveCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;

import java.util.ArrayList;
import java.util.List;

public class ChooseObjectiveCardsState extends GameScreenState {

    public ArrayList<ClientCard> objectivesSelection = new ArrayList<>();

    public ChooseObjectiveCardsState() {
        TUICommands = List.of("'pickObjective <selection>' [1][2] per selezionare il proprio obiettivo segreto",
                "'broadcastMessage <message>' per inviare un messaggio in gioco",
                "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco"
        );
    }

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showObjectiveCardsChoice();
    }

    @Override
    public void pickObjective(int selection){
        //Selection should be [0,1]
        ClientCard card = null;
        try {
            card = objectivesSelection.get(selection);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("nessuna carta obiettivo presente alla posizione specificata");
        }

        ClientController.getInstance().requestToServer(new PickObjectiveCommand(card.ID));
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new PlayerTurnPlayState();
    }
}
