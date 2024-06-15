package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.ServerModel;
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

        System.out.println("[SERVER]: sending ReceiveCardCommand to clients");
        try {
            for (var target : GAME.getPlayers()) {
                target.addCardToHand(initialCardsDeck.draw());
            }
        } catch (EmptyDeckException ignored) {
            //cannot happen as deck has just been created
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
        try {
            placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
        } catch (CardNotInHandException | NotEnoughResourcesException | InvalidCardPositionException ignored) {
            //The placeCard for this player was already done, so the coordinates pair (0,0) is already occupied by
            //a card and the placeCard throws InvalidCardPositionException.
        }
    }

    @Override
    public void transition() {
        for (InGamePlayer target : GAME.getPlayers()) {
            try {
                //TODO: make receiveCardCommand send a single cardID and no longer a list?
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getGoldCardsDeck()));
            } catch (EmptyDeckException ignored) {}
        }

        //TODO: make replaceCardCommand send a single card and no longer a list?

        GAME.generateCommonObjectives();
        GAME.peekFrom(GAME.getResourceCardsDeck());
        GAME.peekFrom(GAME.getGoldCardsDeck());

        //FIXME: move this from here...
        System.out.println("[SERVER]: Sending Top of the Deck, Common and Personal Objectives, GameTransitionCommand to clients in "+ GAME);
        ChooseObjectiveCardsState objectiveState = new ChooseObjectiveCardsState(GAME_CONTROLLER, GAME);
        GAME_CONTROLLER.setState(objectiveState);

        objectiveState.generateObjectivesChoice();
    }
}
