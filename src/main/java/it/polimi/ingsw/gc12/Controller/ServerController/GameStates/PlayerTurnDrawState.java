package it.polimi.ingsw.gc12.Controller.ServerController.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReceiveCardCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReplaceCardCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameController;
import it.polimi.ingsw.gc12.Model.Cards.GoldCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Cards.ResourceCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.InvalidDeckPositionException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnexpectedPlayerException;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnknownStringException;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class PlayerTurnDrawState extends GameState {

    private enum Deck {

        GOLD("gold"), RESOURCE("resource"), VISIBLE_GOLD("gold"), VISIBLE_RESOURCE("resource");

        private final String STRING_MESSAGE;

        Deck(String message){
            this.STRING_MESSAGE = message;
        }
    }

    /*
    * LAMBDA for doing the stated drawing action
    * Deck: where the method tried to draw from
    * Integer: optionalIndex in case it tried to draw from visibleCards
     */
    List<Triplet<Supplier<PlayableCard>, Deck, Integer>> drawActionsRoutine = new ArrayList<>();

    public PlayerTurnDrawState(GameController controller, Game thisGame) {
        super(controller, thisGame, "drawState");

        this.drawActionsRoutine.add(new Triplet<>(() -> tryDraw(() -> {
            try {
                return GAME.getResourceCardsDeck().draw();
            } catch (EmptyDeckException ignored) {}
            return null;
        }), Deck.RESOURCE, 0));

        this.drawActionsRoutine.add(new Triplet<>(() -> tryDraw(() -> {
            try {
                return GAME.getGoldCardsDeck().draw();
            } catch (EmptyDeckException ignored) {
                return null;
            }
        }), Deck.GOLD, 0));

        // Add drawing actions for placed resources
        for (int i = 0; i < GAME.getPlacedResources().length; i++) {
            final int index = i;
            this.drawActionsRoutine.add(new Triplet<>(() -> tryDraw(() -> {
                try {
                    return GAME.drawFrom(GAME.getPlacedResources(), index);
                } catch (EmptyDeckException ignored) {
                    return null;
                }
            }), Deck.VISIBLE_RESOURCE, index));
        }

        // Add drawing actions for placed golds
        for (int i = 0; i < GAME.getPlacedGolds().length; i++) {
            final int index = i;
            this.drawActionsRoutine.add(new Triplet<>(() -> tryDraw(() -> {
                try {
                    return GAME.drawFrom(GAME.getPlacedGolds(), index);
                } catch (EmptyDeckException ignored) {
                    return null;
                }
            }), Deck.VISIBLE_GOLD, index));
        }
    }

    @Override
    public synchronized void drawFrom(InGamePlayer target, String deck) throws UnexpectedPlayerException,
            UnknownStringException, EmptyDeckException {
        if (!target.equals(GAME.getCurrentPlayer()))
            throw new UnexpectedPlayerException();

        PlayableCard drawnCard = null;
        PlayableCard topDeck = null;

        if (deck.trim().equalsIgnoreCase("RESOURCE")) {
            drawnCard = GAME.drawFrom(GAME.getResourceCardsDeck());
            topDeck = GAME.getResourceCardsDeck().peek();
            if (topDeck == null) {
                topDeck = new ResourceCard(-1,0, new HashMap<>(), new HashMap<>());
            }
        } else if (deck.trim().equalsIgnoreCase("GOLD")) {
            drawnCard = GAME.drawFrom(GAME.getGoldCardsDeck());
            topDeck = GAME.getGoldCardsDeck().peek();
            if (topDeck == null) {
                topDeck = new GoldCard(-1, 0, new HashMap<>(), new HashMap<>(), null, null);
            }
        } else
            throw new UnknownStringException();

        target.addCardToHand(drawnCard);

        System.out.println("[SERVER]: Sending drawn card to current player and new top deck card to clients in "+ GAME.toString());
        for (var player : GAME.getActivePlayers()) {
            if (player.equals(target))
                try {
                    GameController.requestToClient(
                            keyReverseLookup(GameController.players, target::equals),
                            new ReceiveCardCommand(List.of(drawnCard.ID))
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            try {
                GameController.requestToClient(
                        keyReverseLookup(GameController.players, player::equals),
                        new ReplaceCardCommand(
                                List.of(
                                        new Triplet<>(topDeck.ID, deck + "_deck", -1)
                                )
                        )
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
        if (!target.equals(GAME.getCurrentPlayer()))
            throw new UnexpectedPlayerException();

        if (position != 0 && position != 1) {
            throw new InvalidDeckPositionException();
        }

        PlayableCard drawnCard;
        PlayableCard replacingCard;
        PlayableCard topDeck;

        if (whichType.trim().equalsIgnoreCase("RESOURCE")) {
            drawnCard = GAME.drawFrom(GAME.getPlacedResources(), position);
            replacingCard = GAME.getPlacedResources()[position];
            if (replacingCard == null) {
                replacingCard = new ResourceCard(-1,0, new HashMap<>(), new HashMap<>());
            }
            topDeck = GAME.getResourceCardsDeck().peek();
            if (topDeck == null) {
                topDeck = new ResourceCard(-1,0, new HashMap<>(), new HashMap<>());
            }
        } else if (whichType.trim().equalsIgnoreCase("GOLD")) {
            drawnCard = GAME.drawFrom(GAME.getPlacedGolds(), position);
            replacingCard = GAME.getPlacedGolds()[position];
            if (replacingCard == null) {
                replacingCard = new GoldCard(-1, 0, new HashMap<>(), new HashMap<>(), null, null);
            }
            topDeck = GAME.getGoldCardsDeck().peek();
            if (topDeck == null) {
                topDeck = new GoldCard(-1, 0, new HashMap<>(), new HashMap<>(), null, null);
            }
        } else
            throw new UnknownStringException();

        target.addCardToHand(drawnCard);

        System.out.println("[SERVER]: Sending drawn card to current player and new visible card to clients in "+ GAME.toString());
        for (var player : GAME.getActivePlayers()) {
            if (player.equals(target))
                try {
                    GameController.requestToClient(
                            keyReverseLookup(GameController.players, target::equals),
                            new ReceiveCardCommand(List.of(drawnCard.ID))
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            try {
                VirtualClient receiver = keyReverseLookup(GameController.players, player::equals);
                GameController.requestToClient(
                        receiver,
                        new ReplaceCardCommand(
                            List.of(
                                new Triplet<>(replacingCard.ID, whichType + "_visible", position)
                            )
                        )
                );
                GameController.requestToClient(
                        receiver,
                         new ReplaceCardCommand(
                            List.of(
                                new Triplet<>(topDeck.ID, whichType + "_deck", -1)
                            )
                         )
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        transition();
        //FIXME: controllare che non si possa giocare due carte nello stesso turno!
        // Poiché il metodo è synchronized e successivamente chiama la transition, dovrebbe essere un'operazione atomica
        // N.B: in teoria quindi questi due metodi sono esclusivi
    }

    @Override
    public void playerDisconnected(InGamePlayer target) {
        PlayableCard drawnCard = null;
        PlayableCard replacingCard = null;
        PlayableCard topDeck = null;
        Triplet<Supplier<PlayableCard>, Deck, Integer> currentActionFormat = null;

        for (Triplet<Supplier<PlayableCard>, Deck, Integer> actionFormat : this.drawActionsRoutine) {
            currentActionFormat = actionFormat;
            drawnCard = actionFormat.getX().get();
            if (drawnCard != null) {
                target.addCardToHand(drawnCard);
                replacingCard = switch(actionFormat.getY()){
                    case Deck.VISIBLE_RESOURCE -> GAME.getPlacedResources()[actionFormat.getZ()];
                    case Deck.VISIBLE_GOLD -> GAME.getPlacedGolds()[actionFormat.getZ()];
                    default -> null;
                };
                topDeck = switch(actionFormat.getY()){
                    case Deck.RESOURCE -> GAME.getResourceCardsDeck().peek();
                    case Deck.GOLD -> GAME.getGoldCardsDeck().peek();
                    case Deck.VISIBLE_RESOURCE -> GAME.getPlacedResources()[actionFormat.getZ()];
                    case Deck.VISIBLE_GOLD -> GAME.getPlacedGolds()[actionFormat.getZ()];
                };

                break;
            }
        }

        //If one of the previous action tried succeeded, you will have a drawnCard and so updates have to be sent
        if(drawnCard != null) {
            //Sending the updated cards to clients and the new TopDeck Card if a drawnAction has been done
            for (var player : GAME.getActivePlayers()) {

                try {
                    VirtualClient receiver = keyReverseLookup(GameController.players, player::equals);
                    if (replacingCard != null)
                        GameController.requestToClient(
                                receiver,
                                new ReplaceCardCommand(
                                        List.of(
                                                new Triplet<>(replacingCard.ID, currentActionFormat.getY().STRING_MESSAGE + "_visible", currentActionFormat.getZ())
                                        )
                                )
                        );
                    if (topDeck == null)
                        topDeck = switch (currentActionFormat.getY()) {
                            case Deck.RESOURCE, Deck.VISIBLE_RESOURCE -> new ResourceCard(-1,0, new HashMap<>(), new HashMap<>());
                            case Deck.GOLD, Deck.VISIBLE_GOLD -> new GoldCard(-1, 0, new HashMap<>(), new HashMap<>(), null, null);
                        };
                    GameController.requestToClient(
                            receiver,
                            new ReplaceCardCommand(
                                    List.of(
                                            new Triplet<>(topDeck.ID, currentActionFormat.getY().STRING_MESSAGE + "_deck", -1)
                                    )
                            )
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        transition();
    }

    private PlayableCard tryDraw(Supplier<PlayableCard> drawAction) {
        return drawAction.get();
    }

    @Override
    public void transition() {
        //REMINDER: se è stato completato il turno di un giocatore disconnesso,
        // il contatore dei turni rimanenti nel caso di finalPhase viene decrementato dalla nextPlayer().

        GAME.nextPlayer();

        //Is final condition satisfied check
        if (GAME.getFinalPhaseCounter() == -1 && GAME.getResourceCardsDeck().isEmpty() && GAME.getGoldCardsDeck().isEmpty()) {
            GAME.initializeFinalPhaseCounter();
            GAME.decreaseFinalPhaseCounter();
        }

        //TODO: segnalare ai giocatori connessi che si stanno giocando i turni finali,
        // attraverso la GameTransitionCommand [Un campo Boolean, il # di Turno in cui finirà la partita,
        // il contatore decrementato?

        if (GAME.getFinalPhaseCounter() == 0) {
            GAME_CONTROLLER.setState(new VictoryCalculationState(GAME_CONTROLLER, GAME));
            GAME_CONTROLLER.getCurrentState().transition();
            return;
        }

        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in "+ GAME.toString());
        notifyTransition(GAME.getActivePlayers(), GAME.getRoundNumber(), GAME.getPlayers().indexOf(GAME.getCurrentPlayer()));

        GAME_CONTROLLER.setState(new PlayerTurnPlayState(GAME_CONTROLLER, GAME));
    }
}
