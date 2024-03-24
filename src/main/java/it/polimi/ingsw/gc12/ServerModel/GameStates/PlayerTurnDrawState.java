package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnknownStringException;

public class PlayerTurnDrawState extends GameState {

    public PlayerTurnDrawState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter);
    }

    @Override
    public void drawFrom(InGamePlayer target, String deck) throws UnexpectedPlayerException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        if (deck.trim().equalsIgnoreCase("RESOURCE")) {
            target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
        } else if (deck.trim().equalsIgnoreCase("GOLD")) {
            target.addCardToHand(GAME.drawFrom(GAME.getGoldCardsDeck()));
        }
    }

    @Override
    public void selectFromVisibleCards(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, InvalidPositionException, UnknownStringException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        if (position != 0 && position != 1) {
            throw new InvalidPositionException();
        }
        if (whichType.trim().equalsIgnoreCase("RESOURCE")) {
            target.addCardToHand(GAME.drawFromVisibleCards(GAME.getPlacedResources(), position));
        } else if (whichType.trim().equalsIgnoreCase("GOLD")) {
            target.addCardToHand(GAME.drawFromVisibleCards(GAME.getPlacedResources(), position));
        } else
            throw new UnknownStringException();
    }

    @Override
    public GameState transition() {
        super.transition();

        if (counter != -1)
            counter--;
        else if (GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty())
            counter = 2 * GAME.getPlayers().size() - currentPlayer - 1;

        if (counter == 0)
            return new VictoryCalculationState(GAME, currentPlayer, counter);

        nextPlayer();
        return new PlayerTurnPlayState(GAME, currentPlayer, counter);
    }
}
