package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.*;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.ChooseInitialCardsState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.GameState;
import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.Server.Server;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Arrays;
import java.util.Optional;

public class GameController extends ServerController {

    public final Game CONTROLLED_GAME;
    private GameState currentGameState;

    public GameController(Game controlledGame) {
        this.CONTROLLED_GAME = controlledGame;
        currentGameState = new ChooseInitialCardsState(this, CONTROLLED_GAME);
    }

    public GameState getCurrentState() {
        return currentGameState;
    }

    public void setState(GameState state) {
        currentGameState = state;
        CONTROLLED_GAME.notifyListeners(new GameTransitionCommand(CONTROLLED_GAME.getRoundNumber(), CONTROLLED_GAME.getCurrentPlayerIndex()));
    }

    private boolean invalidCard(NetworkSession sender, int cardID) {
        if (!ServerModel.cardsList.containsKey(cardID)) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            );
            return true;
        }
        return false;
    }

    @Override
    public synchronized void generatePlayer(NetworkSession sender, String nickname) {
        System.out.println("[SERVER]: sending SetNicknameCommand and RestoreGameCommand to client " + sender);
        sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();

        InGamePlayer targetPlayer = CONTROLLED_GAME.getPlayers().stream()
                .filter((inGamePlayer -> inGamePlayer.getNickname().equals(nickname)))
                .findAny()
                .orElseThrow();

        if (currentGameState instanceof AwaitingReconnectionState)
            //If game was in AwaitingReconnectingState, you need to resume it before sending the DTO
            currentGameState.transition();

        sender.getListener().notified(new RestoreGameCommand(
                CONTROLLED_GAME.generateDTO(targetPlayer),
                currentGameState.getStringEquivalent(), //To let the client understand in which state it has to be recovered to.
                CONTROLLED_GAME.generateTemporaryFieldsToPlayers() //fields related to the players inGame.
        ));

        activePlayers.put(sender, targetPlayer);
        CONTROLLED_GAME.toggleActive(targetPlayer);
        CONTROLLED_GAME.addListener(sender.getListener());
        targetPlayer.addListener(sender.getListener());
    }

    @Override
    public void placeCard(NetworkSession sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        System.out.println("[CLIENT]: PlaceCardCommand received and being executed");
        if (invalidCard(sender, cardID)) return;

        if (Arrays.stream(Side.values()).noneMatch((side) -> side.equals(playedSide))) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid card side")
                    )
            );
            return;
        }

        InGamePlayer targetPlayer = (InGamePlayer) activePlayers.get(sender);
        Card targetCard = ServerModel.cardsList.get(cardID);

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

    @Override
    public void pickObjective(NetworkSession sender, int cardID) {
        System.out.println("[CLIENT]: PickObjectiveCommand received and being executed");

        if (invalidCard(sender, cardID)) return;

        InGamePlayer targetPlayer = (InGamePlayer) activePlayers.get(sender);
        Card targetCard = ServerModel.cardsList.get(cardID);

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

    @Override
    public void drawFromDeck(NetworkSession sender, String deck) {
        System.out.println("[CLIENT]: DrawFromDeckCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) activePlayers.get(sender);

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

    @Override
    public void drawFromVisibleCards(NetworkSession sender, String deck, int position) {
        System.out.println("[CLIENT]: DrawFromVisibleCardsCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) activePlayers.get(sender);

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

    @Override
    public void leaveGame(NetworkSession sender) {
        System.out.println("[CLIENT]: LeaveGameCommand received and being executed");

        sender.getTimeoutTask().cancel();
        CONTROLLED_GAME.removeListener(sender.getListener());

        InGamePlayer targetPlayer = (InGamePlayer) activePlayers.get(sender);
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
                CONTROLLED_GAME.toggleActive(targetPlayer);
                activePlayers.remove(sender);
                inactiveSessions.put(targetPlayer.getNickname(), sender);

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

                System.out.println("[SERVER]: sending ToggleActiveCommand to clients");

                if (CONTROLLED_GAME.getActivePlayers().size() == 1) {
                    CONTROLLED_GAME.notifyListeners(new PauseGameCommand());

                    currentGameState = new AwaitingReconnectionState(this, CONTROLLED_GAME);

                    System.out.println("[SERVER]: Freezing " + CONTROLLED_GAME + " game");
                }
            }
        });
    }

    @Override
    public void broadcastMessage(NetworkSession sender, String message) {
        System.out.println("[CLIENT]: BroadcastMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) activePlayers.get(sender);

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        System.out.println("[SERVER]: sending AddChatMessageCommand to clients");
        ((GameController) sender.getController()).CONTROLLED_GAME
                .notifyListeners(new AddChatMessageCommand(senderPlayer.getNickname(), message, false));
    }

    @Override
    public void directMessage(NetworkSession sender, String receiverNickname, String message) {
        System.out.println("[CLIENT]: DirectMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) activePlayers.get(sender);

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
