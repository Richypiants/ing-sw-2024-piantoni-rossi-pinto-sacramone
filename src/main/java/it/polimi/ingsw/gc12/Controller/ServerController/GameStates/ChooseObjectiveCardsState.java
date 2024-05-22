package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ConfirmSelectionCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;

import java.util.ArrayList;
import java.util.Map;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class ChooseObjectiveCardsState extends GameState {

    private final Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesMap;

    public ChooseObjectiveCardsState(Game thisGame, Map<InGamePlayer, ArrayList<ObjectiveCard>> map) {
        super(thisGame, -1, -1, "objectiveState");
        this.objectivesMap = map;

        //Executing a Random Action for the players disconnected in the Initial State
        for(InGamePlayer player : thisGame.getPlayers().stream().filter( player -> !(player.isActive())).toList())
            playerDisconnected(player);
    }

    @Override
    public synchronized void pickObjective(InGamePlayer targetPlayer, ObjectiveCard objective)
            throws CardNotInHandException, AlreadySetCardException {
        if(!objectivesMap.get(targetPlayer).contains(objective))
            throw new CardNotInHandException();

        if (targetPlayer.getSecretObjective() == null)
            targetPlayer.setSecretObjective(objective);
        else
            throw new AlreadySetCardException();

        if(targetPlayer.isActive()) {
            try {
                GameController.requestToClient(
                        keyReverseLookup(GameController.players, targetPlayer::equals),
                        new ConfirmSelectionCommand(objective.ID));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(GAME.getPlayers().stream()
                .map((player) -> player.getSecretObjective() != null)
                .reduce(true, (a, b) -> a && b)){
            transition();
        }
    }

    @Override
    public void playerDisconnected(InGamePlayer target){
        //The first objectiveCard of the selection is chosen if the player hasn't done it before disconnecting
        //In other case, this function does nothing.

        if(target.getSecretObjective() == null) {
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

        System.out.println("[SERVER]: Sending GameTransitionCommand to active clients in "+ GAME.toString());
        GAME.increaseTurn();
        nextPlayer();
        notifyTransition(GAME.getActivePlayers(), GAME.getTurnNumber(), GAME.getPlayers().indexOf(GAME.getCurrentPlayer()));

        GAME.setState(new PlayerTurnPlayState(GAME, currentPlayer, -1));
    }
}
