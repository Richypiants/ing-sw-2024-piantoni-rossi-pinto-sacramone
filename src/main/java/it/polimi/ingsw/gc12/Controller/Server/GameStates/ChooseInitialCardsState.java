package it.polimi.ingsw.gc12.Controller.Server.GameStates;

import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Model.Server.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Server.Cards.InitialCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

/**
 * Represents the initial state where players choose how to place their starting cards.
 * In this state, each player receives an initial card from a pre-defined deck.
 */
public class ChooseInitialCardsState extends GameState {

    /**
     * Constructs a new ChooseInitialCardsState instance.
     *
     * @param controller The GameController managing the game state transitions and actions.
     * @param thisGame   The Game instance associated with this state.
     */
    public ChooseInitialCardsState(GameController controller, Game thisGame) {
        super(controller, thisGame, "initialState");

        CardDeck<InitialCard> initialCardsDeck = new CardDeck<>(
                ServerModel.CARDS_LIST.values().stream()
                        .filter((card -> card instanceof InitialCard))
                        .map((card) -> (InitialCard) card)
                        .toList()
        );

        try {
            for (var target : GAME.getPlayers()) {
                target.addCardToHand(initialCardsDeck.draw());
            }
        } catch (EmptyDeckException ignored) {
            //Cannot happen as the deck has just been created
            System.exit(-1);
        }
    }

    /**
     * Handles the action of placing a card on the board.
     * Checks if all players have placed their initial cards and triggers a transition if true.
     *
     * @param target      The player attempting to place the card.
     * @param coordinates The coordinates where the card should be placed.
     * @param card        The playable card to be placed.
     * @param playedSide  The side on which the card is played.
     * @throws CardNotInHandException     If the card to be played is not in the player's hand.
     * @throws NotEnoughResourcesException If the player does not have enough resources to play the card.
     * @throws InvalidCardPositionException If the provided coordinates are not valid for placing a card.
     */
    @Override
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {

        GAME.placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), playedSide);

        if(GAME.getPlayers().stream()
                .map((player) -> player.getPlacedCards().containsKey(new GenericPair<>(0, 0)))
                .reduce(true, (a, b) -> a && b)) {
            transition();
        }
    }

    /**
     * Handles the scenario where a player disconnects during the selection of the initial card placement.
     * If the player hasn't placed their initial card before disconnecting, an arbitrary card placement is attempted.
     *
     * @param target The player who disconnected.
     */
    @Override
    public void playerDisconnected(InGamePlayer target){
        //an arbitrary action of placing the initial card is done if the player hasn't done it before disconnecting
        //In other case, this function does nothing.

        if (target.getPlacedCards().get(new GenericPair<>(0, 0)) == null)
            try {
                placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
            } catch (CardNotInHandException | NotEnoughResourcesException | InvalidCardPositionException ignored) {
                System.exit(-1);
            }
    }

    /**
     * Transitions from this state to the next game state.
     * Draws additional cards for each player, generates common objectives and top of the decks.
     */
    @Override
    public void transition() {
        for (InGamePlayer target : GAME.getPlayers()) {
            try {
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getGoldCardsDeck()));
            } catch (EmptyDeckException ignored) {
                System.exit(-1);
            }
        }

        GAME.generateCommonObjectives();
        GAME.peekFrom(GAME.getResourceCardsDeck());
        GAME.peekFrom(GAME.getGoldCardsDeck());

        ChooseObjectiveCardsState objectiveState = new ChooseObjectiveCardsState(GAME_CONTROLLER, GAME);
        GAME_CONTROLLER.setState(objectiveState);

        objectiveState.generateObjectivesChoice();
    }
}
