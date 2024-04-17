package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ReceiveCardCommand;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ReplaceCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidDeckPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnknownStringException;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class PlayerTurnDrawState extends GameState {

    public PlayerTurnDrawState(Game thisGame, int currentPlayer, int counter) {
        super(thisGame, currentPlayer, counter);
    }

    @Override
    public synchronized void drawFrom(InGamePlayer target, String deck) throws UnexpectedPlayerException,
            UnknownStringException, EmptyDeckException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        PlayableCard drawnCard = null;

        if (deck.trim().equalsIgnoreCase("RESOURCE")) {
            drawnCard = GAME.drawFrom(GAME.getResourceCardsDeck());
        } else if (deck.trim().equalsIgnoreCase("GOLD")) {
            drawnCard = GAME.drawFrom(GAME.getGoldCardsDeck());
        } else
            throw new UnknownStringException();

        target.addCardToHand(drawnCard);

        try {
            keyReverseLookup(ServerController.getInstance().players, target::equals)
                    .requestToClient(new ReceiveCardCommand(List.of(drawnCard.ID)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        transition();
        //FIXME: controllare che non si possa pescare due carte nello stesso turno! in teoria rendendo atomica
        // questa intera funzione dovrebbe garantirlo
        // N.B: in teoria quindi questi due metodi sono esclusivi
    }

    //FIXME: change in UML
    @Override
    public synchronized void drawFrom(InGamePlayer target, String whichType, int position)
            throws UnexpectedPlayerException, InvalidDeckPositionException, UnknownStringException, EmptyDeckException {
        if (!target.equals(GAME.getPlayers().get(currentPlayer)))
            throw new UnexpectedPlayerException();

        if (position != 0 && position != 1) {
            throw new InvalidDeckPositionException();
        }

        PlayableCard drawnCard = null;
        PlayableCard replacingCard = null;

        if (whichType.trim().equalsIgnoreCase("RESOURCE")) {
            drawnCard = GAME.drawFrom(GAME.getPlacedResources(), position);
            replacingCard = GAME.getPlacedResources()[position];
        } else if (whichType.trim().equalsIgnoreCase("GOLD")) {
            drawnCard = GAME.drawFrom(GAME.getPlacedGolds(), position);
            replacingCard = GAME.getPlacedGolds()[position];
        } else
            throw new UnknownStringException();

        target.addCardToHand(drawnCard);

        for (var player : GAME.getPlayers())
            if (player.equals(target))
                try {
                    keyReverseLookup(ServerController.getInstance().players, target::equals)
                            .requestToClient(new ReceiveCardCommand(List.of(drawnCard.ID)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            else
                try {
                    keyReverseLookup(ServerController.getInstance().players, target::equals)
                            .requestToClient(
                                    new ReplaceCardCommand(
                                            List.of(
                                                    new Triplet<>(replacingCard.ID, whichType, position)
                                            )
                                    )
                            );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


        transition();
        //FIXME: controllare che non si possa giocare due carte nello stesso turno! in teoria rendendo atomica
        // questa intera funzione dovrebbe garantirlo
        // N.B: in teoria quindi questi due metodi sono esclusivi
    }

    @Override
    public void transition() {
        super.transition();

        if (counter != -1)
            counter--;
        else if (GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty())
            counter = 2 * GAME.getPlayers().size() - currentPlayer - 1;
        //RICORDA: se turno di un disconnesso skip turno MA comunque decrementato qui
        //TODO: send alert a tutti i giocatori che si è entrati nella fase finale?

        if (counter == 0) {
            //TODO: send alert a tutti i giocatori che la partita è finita e si contano i punti? (vediamo)
            GAME.setState(new VictoryCalculationState(GAME, currentPlayer, counter));
            return;
        }

        nextPlayer();
        //TODO: send alert a tutti i giocatori tipo "è il turno di"?
        GAME.setState(new PlayerTurnPlayState(GAME, currentPlayer, counter));
    }
}
