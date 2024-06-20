package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Server.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

public class ChooseInitialCardsState extends GameState {

    public ChooseInitialCardsState(GameController controller, Game thisGame) {
        super(controller, thisGame, "initialState");

        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                ServerModel.CARDS_LIST.values().stream()
                        .filter((card -> card instanceof InitialCard))
                        .map((card) -> (InitialCard) card)
                        .toList()
        );

        try {
            for (var target : GAME.getPlayers()) {
                target.addCardToHand(initialCardsDeck.draw());
            }
        } catch (EmptyDeckException ignored) {
            //Cannot happen as the deck has just been created
            System.exit(-1);
        }
    }

    @Override
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {

        GAME.placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), playedSide);

        if(GAME.getPlayers().stream()
                .map((player) -> player.getPlacedCards().containsKey(new GenericPair<>(0, 0)))
                .reduce(true, (a, b) -> a && b)) {
            transition();
        }
    }

    @Override
    public void playerDisconnected(InGamePlayer target){
        //an arbitrary action of placing the initial card is done if the player hasn't done it before disconnecting
        //In other case, this function does nothing.

        if (target.getPlacedCards().get(new GenericPair<>(0, 0)) == null)
            try {
                placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            } catch (CardNotInHandException | NotEnoughResourcesException | InvalidCardPositionException ignored) {
                System.exit(-1);
            }
    }

    @Override
    public void transition() {
        for (InGamePlayer target : GAME.getPlayers()) {
            try {
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getGoldCardsDeck()));
            } catch (EmptyDeckException ignored) {
                System.exit(-1);
            }
        }

        GAME.generateCommonObjectives();
        GAME.peekFrom(GAME.getResourceCardsDeck());
        GAME.peekFrom(GAME.getGoldCardsDeck());

        ChooseObjectiveCardsState objectiveState = new ChooseObjectiveCardsState(GAME_CONTROLLER, GAME);
        GAME_CONTROLLER.setState(objectiveState);

        objectiveState.generateObjectivesChoice();
    }
}
