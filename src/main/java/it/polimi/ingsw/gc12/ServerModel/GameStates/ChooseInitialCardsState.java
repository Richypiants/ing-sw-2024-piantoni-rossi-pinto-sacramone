package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerController.Controller;
import it.polimi.ingsw.gc12.ServerModel.Cards.CardDeck;
import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
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
    public void generateInitialCard() {
        CardDeck<InitialCard> initialCardsDeck = new CardDeck(JSONParser.deckFromJSONConstructor("initial_cards.json", new TypeToken<ArrayList<ObjectiveCard>>() {
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

        GAME.setState(new DrawStartingHandsState(GAME));
    }
}
