package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ConfirmSelectionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.GameTransitionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.util.ArrayList;
import java.util.Map;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class ChooseObjectiveCardsState extends GameState {

    private final Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesMap;

    public ChooseObjectiveCardsState(Game thisGame, Map<InGamePlayer, ArrayList<ObjectiveCard>> map) {
        super(thisGame, 0, -1);
        this.objectivesMap = map;
    }

    @Override
    public void pickObjective(InGamePlayer targetPlayer, ObjectiveCard objective)
            throws CardNotInHandException, AlreadySetCardException {
        if(!objectivesMap.get(targetPlayer).contains(objective))
            throw new CardNotInHandException();

        if (targetPlayer.getSecretObjective() == null)
            targetPlayer.setSecretObjective(objective);
        else
            throw new AlreadySetCardException();

        try {
            keyReverseLookup(ServerController.getInstance().players, targetPlayer::equals)
                    .requestToClient(new ConfirmSelectionCommand(objective.ID));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //FIXME: dopo timeout e disconnessione: eseguo un'azione random per i player disconnessi
        if(GAME.getPlayers().stream()
                .map((player) -> player.getSecretObjective() != null)
                .reduce(true, (a, b) -> a && b))
            transition();
    }

    @Override
    public void playerDisconnected(InGamePlayer target){
        //The first objectiveCard of the selection is chosen if the player hasn't done it before disconnecting
        //In other case, this function does nothing.

        if(target.getSecretObjective() != null) {
            try {
                pickObjective(target, objectivesMap.get(target).getFirst());
            } catch (CardNotInHandException | AlreadySetCardException ignored) {
                //The pickObjective for this player was already done, so the secretObjective is already set
                //and the pickObjective throws AlreadySetCardException.
            }
        }
    }

    @Override
    public void transition() {
        super.transition();

        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in "+ GAME.toString());
        GAME.increaseTurn();
        for (var targetPlayer : GAME.getPlayers()) {
            //TODO: manage exceptions
            try {
                VirtualClient target = keyReverseLookup(ServerController.getInstance().players, targetPlayer::equals);

                target.requestToClient(
                        new GameTransitionCommand(
                                GAME.getTurnNumber(),
                                GAME.getPlayers().indexOf(GAME.getCurrentPlayer())
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GAME.setState(new PlayerTurnPlayState(GAME, 0, -1));
    }
}
