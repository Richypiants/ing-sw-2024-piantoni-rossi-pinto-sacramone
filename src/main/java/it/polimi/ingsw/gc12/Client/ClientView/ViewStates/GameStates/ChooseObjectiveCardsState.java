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
                "'broadcastMessage <message>' per inviare un messaggio in gioco (max 200 chars)",
                "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco (max 200 chars)"
        );
    }

    @Override
    public void executeState() {
        ClientController.getInstance().view.gameScreen();
    }

    public void restoreScreenState(){
        if(ClientController.getInstance().viewModel.getGame().getOwnObjective() != null) //This is only a safe check,
            //This condition should always be satisfied
            ClientController.getInstance().view.gameScreen();
    }

    @Override
    public void pickObjective(int selection){
        ClientCard card = null;
        try {
            card = objectivesSelection.get(selection - 1);
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
