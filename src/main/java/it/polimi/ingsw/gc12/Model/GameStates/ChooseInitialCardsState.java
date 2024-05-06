package it.polimi.ingsw.gc12.Model.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.*;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.CardDeck;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.CardNotInHandException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidCardPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.NotEnoughResourcesException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class ChooseInitialCardsState extends GameState {

    public ChooseInitialCardsState(Game thisGame) {
        super(thisGame, 0, -1);
    }

    @Override
    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws CardNotInHandException, NotEnoughResourcesException, InvalidCardPositionException {
        target.placeCard(new GenericPair<>(0, 0), target.getCardsInHand().getFirst(), playedSide);

        for (var player : GAME.getPlayers())
            try {
                keyReverseLookup(ServerController.getInstance().players, player::equals)
                        .requestToClient(new PlaceCardCommand(target.getNickname(), coordinates, card.ID, playedSide,
                                target.getOwnedResources(), target.getOpenCorners(), target.getPoints()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        //FIXME: dopo timeout e disconnessione: eseguo un'azione random per i player disconnessi
        if(GAME.getPlayers().stream()
                .map((player) -> player.getPlacedCards().containsKey(new GenericPair<>(0, 0)))
                .reduce(true, (a, b) -> a && b))
            transition();
    }

    @Override
    public void transition() {
        super.transition();

        for (InGamePlayer target : super.GAME.getPlayers()) {
            try {
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getResourceCardsDeck()));
                target.addCardToHand(GAME.drawFrom(GAME.getGoldCardsDeck()));
            } catch (EmptyDeckException e) {
                e.printStackTrace();
                //This cannot happen as the deck is always full at the start of the game
            }

            //TODO: manage exceptions
            try {
                keyReverseLookup(ServerController.getInstance().players, target::equals)
                        .requestToClient(
                                new ReceiveCardCommand(
                                        target.getCardsInHand().stream()
                                                .map((card) -> card.ID)
                                                .toList()
                                )
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CardDeck<ObjectiveCard> objectivesDeck = new CardDeck<>(ServerController.getInstance().cardsList.values().stream()
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

        ArrayList<Triplet<Integer, String, Integer>> objectiveCardPlacements = new ArrayList<>();
        for (int i = 0; i < GAME.getCommonObjectives().length; i++)
            objectiveCardPlacements.add(new Triplet<>(GAME.getCommonObjectives()[i].ID, "Objective", i));

        System.out.println("[SERVER]: Sending Common and Personal Objectives, GameTransitionCommand to clients in "+ GAME.toString());
        for (var targetPlayer : GAME.getPlayers()) {
            //TODO: manage exceptions
            try {
                VirtualClient target = keyReverseLookup(ServerController.getInstance().players, targetPlayer::equals);
                //Sending the common objective cards
                target.requestToClient(new ReplaceCardCommand(objectiveCardPlacements));
                //Sending the personal objective selection
                target.requestToClient(
                                new ReceiveObjectiveChoice(
                                        objectivesSelection.get(targetPlayer).stream()
                                                .map((card) -> card.ID)
                                                .toList()
                                )
                        );
                target.requestToClient(new GameTransitionCommand());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GAME.setState(new ChooseObjectiveCardsState(GAME, objectivesSelection));
    }
}
