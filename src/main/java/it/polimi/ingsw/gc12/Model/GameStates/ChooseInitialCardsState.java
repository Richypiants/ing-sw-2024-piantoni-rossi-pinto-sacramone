package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseInitialCardsState extends GameState {

    public ChooseInitialCardsState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> position, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {
        target.placeCard(new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), playedSide);

        //FIXME: dopo timeout e disconnessione: eseguo un'azione random per i player disconnessi
        if(GAME.getPlayers().stream()
                .map((player) -> player.getPlacedCards().containsKey(new GenericPair<>(0, 0)))
                .reduce(true, (a, b) -> a && b))
            transition();
    }

    @Override
    public void transition() {
        super.transition();

        for (InGamePlayer target : super.GAME.getPlayers()) {
            target.addCardToHand(GAME.getResourceCardsDeck().draw());
            target.addCardToHand(GAME.getResourceCardsDeck().draw());
            target.addCardToHand(GAME.getGoldCardsDeck().draw());
            //TODO: send all cards
        }

        CardDeck<ObjectiveCard> objectivesDeck = new CardDeck<>(ServerController.cardsList.values().stream()
                .filter((card -> card instanceof ObjectiveCard))
                .map((card) -> (ObjectiveCard) card)
                .toList());

        ObjectiveCard[] objectiveCardToGame = new ObjectiveCard[2];
        objectiveCardToGame[0] = objectivesDeck.draw();
        objectiveCardToGame[1] = objectivesDeck.draw();
        GAME.setCommonObjectives(objectiveCardToGame);
        Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesSelection = new HashMap<>();

        for (InGamePlayer target : super.GAME.getPlayers()) {
            ArrayList<ObjectiveCard> objCards = new ArrayList<>();
            objCards.add(objectivesDeck.draw());
            objCards.add(objectivesDeck.draw());
            objectivesSelection.put(target, objCards);
        }

        GAME.setState(new ChooseObjectiveCardsState(GAME, objectivesSelection));
    }
}
