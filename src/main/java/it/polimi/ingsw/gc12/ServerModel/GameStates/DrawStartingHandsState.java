package it.polimi.ingsw.gc12.ServerModel.GameStates;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.CardDeck;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawStartingHandsState extends GameState {
    private final CardDeck OBJECTIVE_CARD_DECK = new CardDeck(JSONParser.deckFromJSONConstructor("objective_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
    }));

    public DrawStartingHandsState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void drawInitialHand() {
        for (InGamePlayer target : super.GAME.getPlayers()) {
            target.addCardToHand((PlayableCard) GAME.getResourceCardsDeck().draw());
            target.addCardToHand((PlayableCard) GAME.getResourceCardsDeck().draw());
            target.addCardToHand((PlayableCard) GAME.getGoldCardsDeck().draw());
        }
    }

    @Override
    public Map<InGamePlayer, ArrayList<ObjectiveCard>> generateObjectiveChoice() {
        ObjectiveCard[] objectiveCardToGame = new ObjectiveCard[2];
        objectiveCardToGame[0] = (ObjectiveCard) OBJECTIVE_CARD_DECK.draw();
        objectiveCardToGame[1] = (ObjectiveCard) OBJECTIVE_CARD_DECK.draw();
        super.GAME.setCommonObjectives(objectiveCardToGame);
        Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesSelection = new HashMap<>();

        for (InGamePlayer target : super.GAME.getPlayers()) {
            ArrayList<ObjectiveCard> objCards = new ArrayList<>();
            objCards.add((ObjectiveCard) OBJECTIVE_CARD_DECK.draw());
            objCards.add((ObjectiveCard) OBJECTIVE_CARD_DECK.draw());
            objectivesSelection.put(target, objCards);
        }

        return objectivesSelection;
    }

    @Override
    public void transition() {
        super.transition();

        GAME.changeState(new ChooseObjectiveCardsState(GAME));
    }
}
