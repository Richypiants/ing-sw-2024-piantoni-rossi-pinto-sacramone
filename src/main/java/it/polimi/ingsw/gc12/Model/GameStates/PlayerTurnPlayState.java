package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class PlayerTurnPlayState extends GameState {

    public PlayerTurnPlayState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter, "playState");
    }

    @Override
    public synchronized void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card,
                                       Side playedSide)
            throws UnexpectedPlayerException, CardNotInHandException, NotEnoughResourcesException,
            InvalidCardPositionException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        target.placeCard(coordinates, card, playedSide);

        System.out.println("[SERVER]: Sending card placed by current player to clients in "+ GAME.toString());
        for (var player : GAME.getActivePlayers())
            try {
                ServerController.getInstance().requestToClient(
                    keyReverseLookup(ServerController.getInstance().players, player::equals),
                        new PlaceCardCommand(target.getNickname(), coordinates, card.ID, playedSide,
                                target.getOwnedResources(), target.getOpenCorners(), target.getPoints()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        transition();
        //FIXME: controllare che non si possa giocare due carte nello stesso turno! in teoria rendendo atomica
        // questa intera funzione dovrebbe garantirlo
    }

    @Override
    public void playerDisconnected(InGamePlayer target){
        transition();
    }

    @Override
    public void transition() {
        super.transition();

        if (finalPhaseCounter == -1)
            if (GAME.getPlayers().get(currentPlayer).getPoints() >= 20)
                finalPhaseCounter = 2 * GAME.getPlayers().size() - currentPlayer;
        //TODO: send alert a tutti i giocatori che si è entrati nella fase finale?

        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in "+ GAME.toString());
        notifyTransition(GAME.getActivePlayers(), GAME.getTurnNumber(), GAME.getPlayers().indexOf(GAME.getCurrentPlayer()));

        GAME.setState(new PlayerTurnDrawState(GAME, currentPlayer, finalPhaseCounter));

        //Check if there's a card that can be drawn, if not, call transition to the just created state.
        if(GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()
                && GAME.getPlacedResources().length == 0 && GAME.getPlacedGolds().length == 0) {

            GAME.getCurrentState().transition();
        }
    }
}
