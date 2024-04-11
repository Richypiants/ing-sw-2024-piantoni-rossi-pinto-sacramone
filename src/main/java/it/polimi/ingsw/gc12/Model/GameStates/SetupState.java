package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;

public class SetupState extends GameState {

    public SetupState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void transition() {
        super.transition();

        //TODO: send commond cards here

        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                ServerController.cardsList.values().stream()
                        .filter((card -> card instanceof InitialCard))
                        .map((card) -> (InitialCard) card)
                        .toList()
        );
        for (InGamePlayer target : super.GAME.getPlayers()) {
            target.addCardToHand(initialCardsDeck.draw());
            //TODO: send all cards
        }

        GAME.setState(new ChooseInitialCardsState(GAME));
    }
}
