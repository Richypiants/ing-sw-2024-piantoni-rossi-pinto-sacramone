package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DrawFromDeckCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DrawFromVisibleCardsCommand;

public class PlayerTurnDrawState extends GameScreenState {

    public PlayerTurnDrawState(){}

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.showField();
    }

    @Override
    public void drawFromDeck(String deck) {
        if (invalidDeck(deck)) throw new IllegalArgumentException("deck fornito da cui pescare invalido");
        try {
            ClientController.getInstance().requestToServer(new DrawFromDeckCommand(deck));
        } catch (Exception e) {
            ClientController.getInstance().view.printError(e);
        }
    }

    @Override
    public void drawFromVisibleCards(String deck, int position) {
        if (invalidDeck(deck)) throw new IllegalArgumentException("area fornita da cui pescare invalida");
        try {
            ClientController.getInstance().requestToServer(new DrawFromVisibleCardsCommand(deck, position));
        } catch (Exception e) {
            ClientController.getInstance().view.printError(e);
        }
    }

    private boolean invalidDeck(String deck) {
        return !(deck.equals("Resource") || deck.equals("Gold"));
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new PlayerTurnPlayState();
    }
}
