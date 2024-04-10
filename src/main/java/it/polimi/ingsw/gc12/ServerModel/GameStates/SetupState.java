package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerController.Controller;
import it.polimi.ingsw.gc12.ServerModel.Cards.CardDeck;
import it.polimi.ingsw.gc12.ServerModel.Cards.InitialCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;

public class SetupState extends GameState {

    public SetupState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void transition() {
        super.transition();

        //TODO: send commond cards here

        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                Controller.cardsList.values().stream()
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
