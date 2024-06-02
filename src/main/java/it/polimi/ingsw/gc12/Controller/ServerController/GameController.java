package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.*;
import it.polimi.ingsw.gc12.Controller.Commands.SetNicknameCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.GameState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.SetupState;
import it.polimi.ingsw.gc12.Model.Cards.Card;
import it.polimi.ingsw.gc12.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.Model.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Model.ServerModel;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Arrays;
import java.util.Optional;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class GameController extends ServerController {

    public final Game CONTROLLED_GAME;
    private GameState currentGameState;

    public GameController(Game controlledGame) {
        this.CONTROLLED_GAME = controlledGame;
        currentGameState = new SetupState(this, CONTROLLED_GAME);
    }

    public GameState getCurrentState() {
        return currentGameState;
    }

    public void setState(GameState state) {
        currentGameState = state;
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
    public void generatePlayer(NetworkSession sender, String nickname) {
        System.out.println("[SERVER]: sending SetNicknameCommand and RestoreGameCommand to client " + sender);
        sender.getListener().notified(new SetNicknameCommand(nickname)); //setNickname();

        InGamePlayer targetPlayer = CONTROLLED_GAME.getPlayers().stream()
                .filter((inGamePlayer -> inGamePlayer.getNickname().equals(nickname)))
                .findAny()
                .orElseThrow();

        if (currentGameState instanceof AwaitingReconnectionState)
            //If game was in AwaitingReconnectingState, you need to resume it before sending the DTO
            ((AwaitingReconnectionState) currentGameState).recoverGame();

        sender.getListener().notified(new RestoreGameCommand(
                keyReverseLookup(model.GAME_CONTROLLERS, this::equals),
                CONTROLLED_GAME.generateDTO(targetPlayer),
                currentGameState.getStringEquivalent(), //To let the client understand in which state it has to be recovered to.
                CONTROLLED_GAME.generateTemporaryFieldsToPlayers() //fields related to the players inGame.
        ));

        for (var player : CONTROLLED_GAME.getActivePlayers())
            if (player.isActive()) {
                NetworkSession targetClient = keyReverseLookup(activePlayers, player::equals);
                targetClient.getListener().notified(new ToggleActiveCommand(nickname)); //toggleActive()
            }

        inactiveSessions.remove(nickname);
        activePlayers.put(sender, targetPlayer);
        ((InGamePlayer) activePlayers.get(sender)).toggleActive();
        //FIXME: restoreGame va chiamata anche quando non c'è il gioco ma il file salvato perchè il server era crashato
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
                currentGameState.placeCard(targetPlayer, coordinates, (PlayableCard) targetCard, playedSide);
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
                currentGameState.pickObjective(targetPlayer, (ObjectiveCard) targetCard);
                //TODO: maybe send a response back to the player?
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
            currentGameState.drawFrom(targetPlayer, deck);
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
            currentGameState.drawFrom(targetPlayer, deck, position);
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

        InGamePlayer targetPlayer = (InGamePlayer) activePlayers.get(sender);

        targetPlayer.toggleActive();
        activePlayers.remove(sender);
        sender.getTimeoutTask().cancel();
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

        for (var player : CONTROLLED_GAME.getActivePlayers()) {
            NetworkSession targetClient = keyReverseLookup(activePlayers, player::equals);
            targetClient.getListener().notified(new ToggleActiveCommand(player.getNickname()));
        }

        int activePlayers = CONTROLLED_GAME.getActivePlayers().size();
        if (activePlayers == 1) {
            for (var player : CONTROLLED_GAME.getActivePlayers())
                keyReverseLookup(ServerController.activePlayers, player::equals).getListener().notified(new PauseGameCommand());

            currentGameState = new AwaitingReconnectionState(this, CONTROLLED_GAME);
            System.out.println("[SERVER]: Freezing " + CONTROLLED_GAME + " game");
        } else if (activePlayers == 0) {
            ((AwaitingReconnectionState) currentGameState).cancelTimerTask();
            for (var player : CONTROLLED_GAME.getPlayers())
                keyReverseLookup(ServerController.activePlayers, player::equals).setController(ConnectionController.getInstance());
            model.GAME_CONTROLLERS.remove(keyReverseLookup(model.GAME_CONTROLLERS, this::equals));
        }
    }

    @Override
    public void broadcastMessage(NetworkSession sender, String message) {
        System.out.println("[CLIENT]: BroadcastMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) activePlayers.get(sender);

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        System.out.println("[SERVER]: sending AddChatMessageCommand to clients");
        for (var inGamePlayer : ((GameController) sender.getController()).CONTROLLED_GAME.getPlayers())
            if (inGamePlayer.isActive())
                keyReverseLookup(activePlayers, inGamePlayer::equals).getListener().notified(
                        new AddChatMessageCommand(senderPlayer.getNickname(), message, false)
                );
    }

    @Override
    public void directMessage(NetworkSession sender, String receiverNickname, String message) {
        System.out.println("[CLIENT]: DirectMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) activePlayers.get(sender);

        Optional<Player> selectedPlayer = activePlayers.values().stream()
                .filter((player) -> player.getNickname().equals(receiverNickname))
                .findAny();

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        if (selectedPlayer.isPresent()) {
            Player receiverPlayer = selectedPlayer.get();
            if (((GameController) sender.getController()).CONTROLLED_GAME
                    .equals(((GameController) keyReverseLookup(activePlayers, receiverPlayer::equals).getController()).CONTROLLED_GAME)) {
                if (((InGamePlayer) receiverPlayer).isActive()) {
                    System.out.println("[SERVER]: sending AddChatMessageCommand to sender and target client");
                    sender.getListener().notified(
                            new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                    );
                    keyReverseLookup(activePlayers, receiverPlayer::equals).getListener().notified(
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
        } else
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Nickname provided has no associated player registered")
                    )
            );
    }
}
