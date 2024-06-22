package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.AlreadySetCardException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Represents the state where players choose their secret objective cards.
 * In this state, each player selects one secret objective card from a pre-defined selection.
 */
public class ChooseObjectiveCardsState extends GameState {
    /** Contains the possible choices that a player can select his secret objective card from.*/
    private Map<InGamePlayer, ArrayList<ObjectiveCard>> objectiveCardsToPlayers;

    /**
     * Constructs a new ChooseObjectiveCardsState instance.
     *
     * @param controller The GameController managing the game state transitions and actions.
     * @param thisGame   The Game instance associated with this state.
     */
    public ChooseObjectiveCardsState(GameController controller, Game thisGame) {
        super(controller, thisGame, "objectiveState");
    }

    /**
     * Generates the selection of secret objective cards for each player.
     * This method is called upon entering the state to initialize the selection process.
     * It also handles disconnected players who are unable to perform a choice during this game phase.
     */
    protected void generateObjectivesChoice(){
        objectiveCardsToPlayers = GAME.generateSecretObjectivesSelection();

        //Executing a Random Action for the players disconnected in the Initial State
        for (InGamePlayer player : GAME.getPlayers().stream().filter(player -> !(player.isActive())).toList())
            playerDisconnected(player);
    }

    /**
     * Allows a player to pick their secret objective card.
     * Checks if all players have selected their objectives and triggers a transition if true.
     *
     * @param targetPlayer The player attempting to pick the objective card.
     * @param objective    The objective card to be picked.
     * @throws CardNotInHandException If the objective card is not in the player's available selection.
     * @throws AlreadySetCardException If the player has already selected a secret objective card.
     */
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

    /**
     * Handles the scenario where a player disconnects during the selection of secret objective cards.
     * If the player has not yet selected a secret objective card, an arbitrary card is chosen for them.
     *
     * @param target The player who disconnected.
     */
    @Override
    public void playerDisconnected(InGamePlayer target){
        //The first objectiveCard of the selection is chosen if the player hasn't done it before disconnecting
        //In other case, this function does nothing.

        if(target.getSecretObjective() == null) {
            try {
                pickObjective(target, objectiveCardsToPlayers.get(target).getFirst());
            } catch (CardNotInHandException | AlreadySetCardException ignored) {
                System.exit(-1);
            }
        }
    }

    /**
     * Executes the transition to the next game state after all players have selected their secret objectives.
     * Increases the round count, advances to the next player, and transitions to the player turn play state.
     */
    @Override
    public void transition() {
        GAME.increaseRound();
        GAME.nextPlayer();

        GAME_CONTROLLER.setState(new PlayerTurnPlayState(GAME_CONTROLLER, GAME));
    }
}
