package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReceiveCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;

import java.util.List;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class SetupState extends GameState {

    public SetupState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void transition() {
        super.transition();

        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                ServerController.getInstance().cardsList.values().stream()
                        .filter((card -> card instanceof InitialCard))
                        .map((card) -> (InitialCard) card)
                        .toList()
        );

        System.out.println("[SERVER]: sending ReceiveCardCommand to clients");
        try {
            for (var target : GAME.getPlayers()) {
                InitialCard tmp = initialCardsDeck.draw();
                target.addCardToHand(tmp);

                //TODO: manage exceptions
                try {
                    keyReverseLookup(ServerController.getInstance().players, target::equals)
                            .requestToClient(new ReceiveCardCommand(List.of(tmp.ID)));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (EmptyDeckException e) {
            //cannot happen as deck has just been created
            e.printStackTrace();
        }

        GAME.setState(new ChooseInitialCardsState(GAME));
    }
}
