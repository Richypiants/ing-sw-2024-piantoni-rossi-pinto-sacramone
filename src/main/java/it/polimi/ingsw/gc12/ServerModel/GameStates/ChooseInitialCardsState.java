package it.polimi.ingsw.gc12.ServerModel.GameStates;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.CardDeck;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.JSONParser;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;

public class ChooseInitialCardsState extends GameState {

    public ChooseInitialCardsState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void generateInitialCard() {
        CardDeck initialCardsDeck = new CardDeck(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
        }));
        for (InGamePlayer target : super.GAME.getPlayers())
            target.addCardToHand((PlayableCard) initialCardsDeck.draw());
    }

    @Override
    public void placeInitialCard(InGamePlayer player, Side side) {
        //TODO: check che non l'abbia già giocata
        player.placeCard(new GenericPair<>(0, 0), player.getCardsInHand().getFirst(), side);
    }

    @Override
    public void transition() {
        super.transition();

        GAME.changeState(new DrawStartingHandsState(GAME));
    }
}
