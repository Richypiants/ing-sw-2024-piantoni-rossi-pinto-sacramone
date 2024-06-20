package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;

import java.util.ArrayList;
import java.util.Map;

public class ChooseObjectiveCardsState extends GameState {
    private Map<InGamePlayer, ArrayList<ObjectiveCard>> objectiveCardsToPlayers;

    public ChooseObjectiveCardsState(GameController controller, Game thisGame) {
        super(controller, thisGame, "objectiveState");
    }

    protected void generateObjectivesChoice(){
        objectiveCardsToPlayers = GAME.generateSecretObjectivesSelection();

        //Executing a Random Action for the players disconnected in the Initial State
        for (InGamePlayer player : GAME.getPlayers().stream().filter(player -> !(player.isActive())).toList())
            playerDisconnected(player);
    }

    @Override
    public void pickObjective(InGamePlayer targetPlayer, ObjectiveCard objective)
            throws CardNotInHandException, AlreadySetCardException {
        if(!objectiveCardsToPlayers.get(targetPlayer).contains(objective))
            throw new CardNotInHandException();

        if (targetPlayer.getSecretObjective() == null)
            targetPlayer.setSecretObjective(objective);
        else
            throw new AlreadySetCardException();

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
                pickObjective(target, objectiveCardsToPlayers.get(target).getFirst());
            } catch (CardNotInHandException | AlreadySetCardException ignored) {
                //FIXME: fake, if exception is caught this method fails and leaveGame isn't completed and doesn't transition to AwaitingReconnectionState!
                //The pickObjective for this player was already done, so the secretObjective is already set
                //and the pickObjective throws AlreadySetCardException.
                //FIXME: non sono sicuro che ci vada qui
                System.exit(-1);
            }
        }
    }

    @Override
    public void transition() {
        GAME.increaseRound();
        GAME.nextPlayer();

        GAME_CONTROLLER.setState(new PlayerTurnPlayState(GAME_CONTROLLER, GAME));
    }
}
