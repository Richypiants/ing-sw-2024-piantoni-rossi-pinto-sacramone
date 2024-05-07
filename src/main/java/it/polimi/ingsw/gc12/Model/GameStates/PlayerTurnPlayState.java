package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.GameTransitionCommand;
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
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class PlayerTurnPlayState extends GameState {

    public PlayerTurnPlayState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter);
    }

    @Override
    public synchronized void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card,
                                       Side playedSide)
            throws UnexpectedPlayerException, CardNotInHandException, NotEnoughResourcesException,
            InvalidCardPositionException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        System.out.println("[SERVER]: Sending card placed by current player to clients in "+ GAME.toString());
        for (var player : GAME.getPlayers())
            try {
                keyReverseLookup(ServerController.getInstance().players, player::equals)
                        .requestToClient(new PlaceCardCommand(target.getNickname(), coordinates, card.ID, playedSide,
                                target.getOwnedResources(), target.getOpenCorners(), target.getPoints()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        target.placeCard(coordinates, card, playedSide);
        transition();
        //FIXME: controllare che non si possa giocare due carte nello stesso turno! in teoria rendendo atomica
        // questa intera funzione dovrebbe garantirlo
    }

    @Override
    public void transition() {
        super.transition();

        if (counter == -1)
            if (GAME.getPlayers().get(currentPlayer).getPoints() >= 20)
                counter = 2 * GAME.getPlayers().size() - currentPlayer;
        //TODO: send alert a tutti i giocatori che si è entrati nella fase finale?

        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in "+ GAME.toString());
        for (var targetPlayer : GAME.getPlayers()) {
            //TODO: manage exceptions
            try {
                VirtualClient target = keyReverseLookup(ServerController.getInstance().players, targetPlayer::equals);
                target.requestToClient(new GameTransitionCommand());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GAME.setState(new PlayerTurnDrawState(GAME, currentPlayer, counter));
    }
}
