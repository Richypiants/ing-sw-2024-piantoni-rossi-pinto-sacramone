package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DrawFromDeckCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DrawFromVisibleCardsCommand;

import java.util.List;

public class PlayerTurnDrawState extends GameScreenState {

    public PlayerTurnDrawState() {
        TUICommands = ClientController.getInstance().isThisClientTurn() ?
                List.of(
                        "'drawFromDeck <deck>' [resource][gold]",
                        "'drawFromVisibleCards <deck> <position>' [resource][gold] [1][2]",
                        "'broadcastMessage <message>' per inviare un messaggio in gioco",
                        "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco") :
                List.of(
                        "'broadcastMessage <message>' per inviare un messaggio in gioco",
                        "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco");
    }

    @Override
    public void executeState() {
        super.executeState();
        ClientController.getInstance().view.gameScreen();
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
        if (position != 1 && position != 2) throw new IllegalArgumentException("position fornita da cui pescare invalida");
        try {
            ClientController.getInstance().requestToServer(new DrawFromVisibleCardsCommand(deck, position - 1));
        } catch (Exception e) {
            ClientController.getInstance().view.printError(e);
        }
    }

    private boolean invalidDeck(String deck) {
        return !(deck.equalsIgnoreCase("resource") || deck.equalsIgnoreCase("gold"));
    }

    @Override
    public void transition() {
        /*if(...){
            increaseRound();
        }*/
        ClientController.getInstance().viewState = new PlayerTurnPlayState();
    }
}
