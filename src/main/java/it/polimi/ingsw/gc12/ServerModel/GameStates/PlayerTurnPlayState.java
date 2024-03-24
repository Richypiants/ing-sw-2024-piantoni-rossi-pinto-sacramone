package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.ServerModel.Game;
import it.polimi.ingsw.gc12.ServerModel.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

public class PlayerTurnPlayState extends GameState {

    public PlayerTurnPlayState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter);
    }

    @Override
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card,
                          Side side) throws UnexpectedPlayerException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        target.placeCard(coordinates, card, side);
        //TODO: gestire quando fallisce l'aggiunta?
        //FIXME: controllare che non si possa giocare due carte nello stesso turno!
    }

    @Override
    public GameState transition() {
        super.transition();

        if (counter == -1)
            if (GAME.getPlayers().get(currentPlayer).getPoints() >= 20)
                counter = 2 * GAME.getPlayers().size() - currentPlayer;

        return new PlayerTurnDrawState(GAME, currentPlayer, counter);
    }
}
