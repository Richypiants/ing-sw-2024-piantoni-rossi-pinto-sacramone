package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.GameTransitionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReceiveObjectiveChoice;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReplaceCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class ChooseInitialCardsState extends GameState {

    public ChooseInitialCardsState(GameController controller, Game thisGame) {
        super(controller, thisGame, "initialState");
    }

    @Override
    public synchronized void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {

        GAME.placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), playedSide);

        if(GAME.getPlayers().stream()
                .map((player) -> player.getPlacedCards().containsKey(new GenericPair<>(0, 0)))
                .reduce(true, (a, b) -> a && b)) {
            transition();
        }
    }

    @Override
    public void playerDisconnected(InGamePlayer target){
        //an arbitrary action of placing the initial card is done if the player hasn't done it before disconnecting
        //In other case, this function does nothing.
        try {
            placeCard(target, new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), Side.FRONT);
        } catch (CardNotInHandException | NotEnoughResourcesException | InvalidCardPositionException ignored) {
            //The placeCard for this player was already done, so the coordinates pair (0,0) is already occupied by
            //a card and the placeCard throws InvalidCardPositionException.
        }
    }

    @Override
    public void transition() {
        for (InGamePlayer target : GAME.getPlayers()) {
            try {
                //TODO: make receiveCardCommand send a single cardID and no longer a list?
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getGoldCardsDeck()));
            } catch (EmptyDeckException e) {
                e.printStackTrace();
                //This cannot happen as the deck is always full at the start of the game
            }
        }

        CardDeck<ObjectiveCard> objectivesDeck = new CardDeck<>(ServerModel.cardsList.values().stream()
                .filter((card -> card instanceof ObjectiveCard))
                .map((card) -> (ObjectiveCard) card)
                .toList());

        ObjectiveCard[] objectiveCardsToGame = new ObjectiveCard[2];
        Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesSelection = new HashMap<>();

        try {
            objectiveCardsToGame[0] = objectivesDeck.draw();
            objectiveCardsToGame[1] = objectivesDeck.draw();

            GAME.setCommonObjectives(objectiveCardsToGame);

            for (InGamePlayer target : super.GAME.getPlayers()) {
                ArrayList<ObjectiveCard> personalObjectiveCards = new ArrayList<>();
                personalObjectiveCards.add(objectivesDeck.draw());
                personalObjectiveCards.add(objectivesDeck.draw());
                objectivesSelection.put(target, personalObjectiveCards);
            }
        } catch(EmptyDeckException e){
            //cannot happen as deck has just been created
            e.printStackTrace();
        }

        //TODO: make replaceCardCommand send a single card and no longer a list?
        ArrayList<Triplet<Integer, String, Integer>> topDeckAndObjectiveCardPlacements = new ArrayList<>();
        for (int i = 0; i < GAME.getCommonObjectives().length; i++)
            topDeckAndObjectiveCardPlacements.add(new Triplet<>(GAME.getCommonObjectives()[i].ID, "objective_visible", i));

        topDeckAndObjectiveCardPlacements.add(new Triplet<>(GAME.getResourceCardsDeck().peek().ID, "resource_deck", -1));
        topDeckAndObjectiveCardPlacements.add(new Triplet<>(GAME.getGoldCardsDeck().peek().ID, "gold_deck", -1));

        //FIXME: these ones are difficult to move from here...
        System.out.println("[SERVER]: Sending Top of the Deck, Common and Personal Objectives, GameTransitionCommand to clients in "+ GAME.toString());
        for (var targetPlayer : GAME.getActivePlayers()) {
            NetworkSession target = keyReverseLookup(GameController.activePlayers, targetPlayer::equals);
            //Sending the common objective cards and the Top of the Deck
            target.getListener().notified(new ReplaceCardCommand(topDeckAndObjectiveCardPlacements));
            //Request view state transition to client
            target.getListener().notified(new GameTransitionCommand());
            //Sending the personal objective selection
            //FIXME: why this command still has to be sent here?
            target.getListener().notified(
                    new ReceiveObjectiveChoice(
                            objectivesSelection.get(targetPlayer).stream()
                                    .map((card) -> card.ID)
                                    .toList()
                    )
            );
        }

        GAME_CONTROLLER.setState(new ChooseObjectiveCardsState(GAME_CONTROLLER, GAME, objectivesSelection));
    }
}
