package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReceiveCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.ServerModel;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;

import java.util.List;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class SetupState extends GameState {

    public SetupState(GameController controller, Game thisGame) {
        super(controller, thisGame, "setupState");
    }

    @Override
    public void transition() {
        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                ServerModel.cardsList.values().stream()
                        .filter((card -> card instanceof InitialCard))
                        .map((card) -> (InitialCard) card)
                        .toList()
        );

        System.out.println("[SERVER]: sending ReceiveCardCommand to clients");
        try {
            for (var target : GAME.getActivePlayers()) {
                InitialCard tmp = initialCardsDeck.draw();
                target.addCardToHand(tmp);

                //TODO: manage exceptions
                try {
                    GameController.requestToClient(
                            keyReverseLookup(GameController.players, target::equals),
                            new ReceiveCardCommand(List.of(tmp.ID)));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (EmptyDeckException e) {
            //cannot happen as deck has just been created
            e.printStackTrace();
        }

        GAME_CONTROLLER.setState(new ChooseInitialCardsState(GAME_CONTROLLER, GAME));
    }
}
