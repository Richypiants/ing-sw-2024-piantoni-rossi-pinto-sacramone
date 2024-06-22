package it.polimi.ingsw.gc12.Controller.Server;

import it.polimi.ingsw.gc12.Commands.ClientCommands.*;
import it.polimi.ingsw.gc12.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Controller.Server.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.Controller.Server.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.Controller.Server.GameStates.GameState;
import it.polimi.ingsw.gc12.Model.Server.Cards.Card;
import it.polimi.ingsw.gc12.Model.Server.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Server.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Server.Game;
import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Server.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.Server.Server;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.Optional;

/**
 * The {@code GameController} class extends the {@link ServerController} and manages the game state,
 * handling player actions such as placing cards, picking objectives, and drawing from decks.
 * <p>
 * This class ensures synchronization and state management for the ongoing game.
 */
public class GameController extends ServerController {

    /**
     * The game controlled by this controller.
     */
    public final Game CONTROLLED_GAME;

    /**
     * The current state of the game.
     */
    private GameState currentGameState;

    /**
     * Constructs a {@code GameController} for the specified game.
     *
     * @param controlledGame the game to be controlled
     */
    public GameController(Game controlledGame) {
        this.CONTROLLED_GAME = controlledGame;
        currentGameState = new ChooseInitialCardsState(this, CONTROLLED_GAME);
    }

    /**
     * Returns the current state of the game.
     *
     * @return the current game state
     */
    public GameState getCurrentState() {
        return currentGameState;
    }

    /**
     * Sets the state of the game and notifies clients of the state transition.
     *
     * @param state the new state of the game
     */
    public void setState(GameState state) {
        currentGameState = state;
        System.out.println("[SERVER]: Sending GameTransitionCommand to clients in " + CONTROLLED_GAME);
        CONTROLLED_GAME.notifyListeners(new GameTransitionCommand(CONTROLLED_GAME.getRoundNumber(), CONTROLLED_GAME.getCurrentPlayerIndex(), CONTROLLED_GAME.getFinalPhaseCounter()));
    }

    /**
     * Checks if the provided card ID is invalid.
     *
     * @param sender the network session of the client
     * @param cardID the card ID to check
     * @return {@code true} if the card ID is invalid, {@code false} otherwise
     */
    private boolean invalidCard(NetworkSession sender, int cardID) {
        if (!ServerModel.CARDS_LIST.containsKey(cardID)) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            );
            return true;
        }
        return false;
    }

    /**
     * Generates a new player with the given nickname, sends the relevant commands to the client,
     * and updates the server model with the new player.
     *
     * @param sender   the network session of the client
     * @param nickname the nickname for the new player
     */
    @Override
    public synchronized void generatePlayer(NetworkSession sender, String nickname) {
        //This is necessary because the createPlayer might have detected this player's INACTIVE_SESSION, but then this
        // controller's VictoryCalculationState.transition() might be executed (entirely) before this function,
        // thus eliminating the controller of the player being restored and invalidating both the game and the
        // reconnection, which would still happen
        if (this.equals(sender.getController())) {
            System.out.println("[SERVER]: sending SetNicknameCommand and RestoreGameCommand to client " + sender);
            sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();

            InGamePlayer targetPlayer = CONTROLLED_GAME.getPlayers().stream()
                    .filter((inGamePlayer -> inGamePlayer.getNickname().equals(nickname)))
                    .findAny()
                    .orElseThrow();

            if (currentGameState instanceof AwaitingReconnectionState)
                //If game was in AwaitingReconnectingState, you need to resume it before sending the DTO
                currentGameState.transition();

            CONTROLLED_GAME.setPlayerActivity(targetPlayer, true);

            sender.getListener().notified(new RestoreGameCommand(
                    CONTROLLED_GAME.generateDTO(targetPlayer),
                    currentGameState.getStringEquivalent(), //To let the client understand in which state it has to be recovered to.
                    CONTROLLED_GAME.generateTemporaryFieldsToPlayers() //fields related to the players inGame.
            ));

            sender.setPlayer(targetPlayer);
            putActivePlayer(sender, targetPlayer);
            CONTROLLED_GAME.addListener(sender.getListener());
            targetPlayer.addListener(sender.getListener());
        } else
            ConnectionController.getInstance().generatePlayer(sender, nickname);
        //If the controller had indeed been invalidated, a normal generatePlayer(...) needs to be executed
    }

    /**
     * Places a card on the game board at the specified coordinates.
     *
     * @param sender      the network session of the client
     * @param coordinates the coordinates to place the card
     * @param cardID      the ID of the card to be placed
     * @param playedSide  the side of the card to be played
     */
    @Override
    public void placeCard(NetworkSession sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        System.out.println("[CLIENT]: PlaceCardCommand received and being executed");
        if (invalidCard(sender, cardID)) return;

        InGamePlayer targetPlayer = (InGamePlayer) sender.getPlayer();
        Card targetCard = ServerModel.CARDS_LIST.get(cardID);

        if (targetCard instanceof PlayableCard)
            try {
                synchronized (this) {
                    currentGameState.placeCard(targetPlayer, coordinates, (PlayableCard) targetCard, playedSide);
                }
            } catch (ForbiddenActionException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot place a card in this state")
                        )
                );
            } catch (UnexpectedPlayerException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Not this player's turn")
                        )
                );
            } catch (CardNotInHandException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                );
            } catch (NotEnoughResourcesException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new NotEnoughResourcesException(
                                        "Player doesn't own the required resources to play the provided card"
                                )
                        )
                );
            } catch (InvalidCardPositionException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new InvalidCardPositionException("Provided coordinates are not valid for placing a card")
                        )
                );
            }
        else {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Provided card is not of a playable type")
                    )
            );
        }
    }

    /**
     * Picks an objective card for the player.
     *
     * @param sender the network session of the client
     * @param cardID the ID of the card to be picked
     */
    @Override
    public void pickObjective(NetworkSession sender, int cardID) {
        System.out.println("[CLIENT]: PickObjectiveCommand received and being executed");

        if (invalidCard(sender, cardID)) return;

        InGamePlayer targetPlayer = (InGamePlayer) sender.getPlayer();
        Card targetCard = ServerModel.CARDS_LIST.get(cardID);

        if (targetCard instanceof ObjectiveCard)
            try {
                synchronized (this) {
                    currentGameState.pickObjective(targetPlayer, (ObjectiveCard) targetCard);
                }
            } catch (ForbiddenActionException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot pick an objective card in this state")
                        )
                );
            } catch (CardNotInHandException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                );
            } catch (AlreadySetCardException e) {
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new AlreadySetCardException("Secret objective already chosen")
                        )
                );
            }
        else {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Card with provided cardID is not of type ObjectiveCard")
                    )
            );
        }
    }

    /**
     * Draws a card from the specified deck for the player.
     *
     * @param sender the network session of the client
     * @param deck   the name of the deck to draw from
     */
    @Override
    public void drawFromDeck(NetworkSession sender, String deck) {
        System.out.println("[CLIENT]: DrawFromDeckCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) sender.getPlayer();

        try {
            synchronized (this) {
                currentGameState.drawFrom(targetPlayer, deck);
            }
        } catch (ForbiddenActionException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a card from a deck in this state")
                    )
            );
        } catch (UnexpectedPlayerException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            );
        } catch (UnknownStringException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such deck exists")
                    )
            );
        } catch (EmptyDeckException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new EmptyDeckException("Selected deck is empty")
                    )
            );
        }
    }

    /**
     * Draws a visible card from the specified deck for the player.
     *
     * @param sender   the network session of the client
     * @param deck     the name of the deck to draw from
     * @param position the position of the card in the deck
     */
    @Override
    public void drawFromVisibleCards(NetworkSession sender, String deck, int position) {
        System.out.println("[CLIENT]: DrawFromVisibleCardsCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) sender.getPlayer();

        try {
            synchronized (this) {
                currentGameState.drawFrom(targetPlayer, deck, position);
            }
        } catch (ForbiddenActionException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a visible card in this state")
                    )
            );
        } catch (UnexpectedPlayerException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            );
        } catch (InvalidDeckPositionException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new InvalidDeckPositionException("Cannot understand which card to draw")
                    )
            );
        } catch (UnknownStringException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such placed cards exist")
                    )
            );
        } catch (EmptyDeckException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new EmptyDeckException("No card in selected slot")
                    )
            );
        }
    }

    /**
     * Removes a player from the game, handles the necessary state transitions,
     * and notifies the relevant components.
     *
     * @param sender the network session of the client
     */
    @Override
    public void leaveGame(NetworkSession sender) {
        System.out.println("[CLIENT]: LeaveGameCommand received and being executed");

        sender.getTimeoutTask().cancel();
        CONTROLLED_GAME.removeListener(sender.getListener());

        InGamePlayer targetPlayer = (InGamePlayer) sender.getPlayer();
        targetPlayer.removeListener(sender.getListener());

        //It is needed: when we get here because a listener has failed to notify a game action, that same thread (which
        // still holds the lock for this controller instance) needs to immediately remove that listener before any other
        // action of the game (usually a GameTransitionCommand) is sent, but at the same time the switch to the
        // AwaitingReconnectionState MUST happen after that GameTransition has gone to the next state, otherwise the
        // wait state will be overwritten by the following state. We solve this problem by making the transition to
        // AwaitingReconnectionState to another thread and forcing it to wait until the transition() function
        // releases the lock.
        Server.getInstance().commandExecutorsPool.submit(() -> {
            synchronized (this) {
                CONTROLLED_GAME.setPlayerActivity(targetPlayer, false);
                removeActivePlayer(sender);
                INACTIVE_SESSIONS.put(targetPlayer.getNickname(), sender);

                /*Checking if the disconnection happened during the sender turn. If so:
                 * 1. If it was during PlayerTurnPlayState,
                 *   the game will transition() to the PlayerTurnDrawState
                 *   that will check if the player is inactive and then transition as well.
                 *
                 * 2. If it was during PlayerTurnDrawState,
                 *    a card has to be drawn following a standard routine, if no card can be drawn, transition()
                 *    without giving any card.
                 *
                 * If the player disconnected in another player's turn, there's no problem
                 * because the players' activity is managed by the GameStates.
                 * */

                if (CONTROLLED_GAME.getCurrentPlayer() == null || CONTROLLED_GAME.getCurrentPlayer().equals(targetPlayer))
                    currentGameState.playerDisconnected(targetPlayer);

                if (CONTROLLED_GAME.getActivePlayers().size() == 1 && !(currentGameState instanceof AwaitingReconnectionState)) {
                    System.out.println("[SERVER]: Freezing " + CONTROLLED_GAME + " game");
                    CONTROLLED_GAME.notifyListeners(new PauseGameCommand());

                    currentGameState = new AwaitingReconnectionState(this, CONTROLLED_GAME);
                }
            }
        });
    }

    /**
     * Broadcasts a message to all players in the game.
     *
     * @param sender  the network session of the client
     * @param message the message to broadcast
     */
    @Override
    public void broadcastMessage(NetworkSession sender, String message) {
        System.out.println("[CLIENT]: BroadcastMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) sender.getPlayer();

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        System.out.println("[SERVER]: sending AddChatMessageCommand to clients");
        ((GameController) sender.getController()).CONTROLLED_GAME
                .notifyListeners(new AddChatMessageCommand(senderPlayer.getNickname(), message, false));
    }

    /**
     * Sends a direct message to a specific player in the game.
     *
     * @param sender           the network session of the client
     * @param receiverNickname the nickname of the player to receive the message
     * @param message          the message to send
     */
    @Override
    public void directMessage(NetworkSession sender, String receiverNickname, String message) {
        System.out.println("[CLIENT]: DirectMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) sender.getPlayer();

        Optional<InGamePlayer> selectedPlayer = ((GameController) sender.getController()).CONTROLLED_GAME
                .getPlayers().stream()
                .filter((player) -> player.getNickname().equals(receiverNickname))
                .findAny();

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        if (selectedPlayer.isPresent()) {
            if (selectedPlayer.get().isActive()) {
                System.out.println("[SERVER]: sending AddChatMessageCommand to sender and target client");
                senderPlayer.notifyListeners(
                        new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                );
                selectedPlayer.get().notifyListeners(
                        new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                );
            } else
                sender.getListener().notified(
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Nickname provided has no active player associated in this game")
                        )
                );
        } else
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Nickname provided has no associated player in this game")
                    )
            );
    }
}
