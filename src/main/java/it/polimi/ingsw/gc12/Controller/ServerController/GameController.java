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
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.Arrays;
import java.util.Optional;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class GameController extends ServerController {

    private final Game CONTROLLED_GAME;
    //TODO: move gamestates in here instead of having them in Game (also move back currentPlayer and round in Game after this?)
    private GameState currentGameState;

    public GameController(Game controlledGame) {
        this.CONTROLLED_GAME = controlledGame;
        currentGameState = new SetupState(CONTROLLED_GAME);
    }

    private boolean invalidCard(VirtualClient sender, int cardID) {
        if (!cardsList.containsKey(cardID)) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Provided cardID is not associated to an existing card")
                    )
            );
            return true;
        }
        return false;
    }

    @Override
    public void generatePlayer(VirtualClient sender, String nickname) {
        System.out.println("[SERVER]: sending SetNicknameCommand and RestoreGameCommand to client " + sender);
        requestToClient(sender, new SetNicknameCommand(nickname)); //setNickname();

        if (CONTROLLED_GAME.getCurrentState() instanceof AwaitingReconnectionState)
            //If game was in AwaitingReconnectingState, you need to resume it before sending the DTO
            ((AwaitingReconnectionState) CONTROLLED_GAME.getCurrentState()).recoverGame();

        requestToClient(sender, new RestoreGameCommand(
                keyReverseLookup(lobbiesAndGames, CONTROLLED_GAME::equals),
                CONTROLLED_GAME.generateDTO((InGamePlayer) players.get(sender)),
                CONTROLLED_GAME.getCurrentState().getStringEquivalent(), //To let the client understand in which state it has to be recovered to.
                CONTROLLED_GAME.generateTemporaryFieldsToPlayers() //fields related to the players inGame.
        ));

        for (var player : CONTROLLED_GAME.getActivePlayers())
            if (player.isActive()) {
                VirtualClient targetClient = keyReverseLookup(players, player::equals);
                requestToClient(targetClient, new ToggleActiveCommand(nickname)); //toggleActive()
            }

        ((InGamePlayer) players.get(sender)).toggleActive();
        //FIXME: restoreGame va chiamata anche quando non c'è il gioco ma il file salvato perchè il server era crashato
    }

    @Override
    public void placeCard(VirtualClient sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide) {
        System.out.println("[CLIENT]: PlaceCardCommand received and being executed");
        if (invalidCard(sender, cardID)) return;

        if (Arrays.stream(Side.values()).noneMatch((side) -> side.equals(playedSide))) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new IllegalArgumentException("Invalid card side")
                    )
            );
            return;
        }

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = ((GameController) playersToControllers.get(targetPlayer)).CONTROLLED_GAME;
        Card targetCard = cardsList.get(cardID);

        if (targetCard instanceof PlayableCard)
            try {
                targetGame.getCurrentState().placeCard(targetPlayer, coordinates, (PlayableCard) targetCard, playedSide);
            } catch (ForbiddenActionException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot place a card in this state")
                        )
                );
            } catch (UnexpectedPlayerException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new UnexpectedPlayerException("Not this player's turn")
                        )
                );
            } catch (CardNotInHandException e) {
                requestToClient(sender,
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                );
            } catch (NotEnoughResourcesException e) {
                requestToClient(sender,
                        new ThrowExceptionCommand(
                                new NotEnoughResourcesException(
                                        "Player doesn't own the required resources to play the provided card"
                                )
                        )
                );
            } catch (InvalidCardPositionException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new InvalidCardPositionException("Provided coordinates are not valid for placing a card")
                        )
                );
            }
        else {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Provided card is not of a playable type")
                    )
            );
        }
    }

    @Override
    public void pickObjective(VirtualClient sender, int cardID) {
        System.out.println("[CLIENT]: PickObjectiveCommand received and being executed");

        if (invalidCard(sender, cardID)) return;

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = ((GameController) playersToControllers.get(targetPlayer)).CONTROLLED_GAME;
        Card targetCard = cardsList.get(cardID);

        if (targetCard instanceof ObjectiveCard)
            try {
                targetGame.getCurrentState().pickObjective(targetPlayer, (ObjectiveCard) targetCard);
                //TODO: maybe send a response back to the player?
            } catch (ForbiddenActionException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new ForbiddenActionException("Cannot pick an objective card in this state")
                        )
                );
            } catch (CardNotInHandException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new CardNotInHandException("Card with provided cardID is not in player's hand")
                        )
                );
            } catch (AlreadySetCardException e) {
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new AlreadySetCardException("Secret objective already chosen")
                        )
                );
            }
        else {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidCardTypeException("Card with provided cardID is not of type ObjectiveCard")
                    )
            );
        }
    }

    @Override
    public void drawFromDeck(VirtualClient sender, String deck) {
        System.out.println("[CLIENT]: DrawFromDeckCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = ((GameController) playersToControllers.get(targetPlayer)).CONTROLLED_GAME;

        try {
            targetGame.getCurrentState().drawFrom(targetPlayer, deck);
        } catch (ForbiddenActionException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a card from a deck in this state")
                    )
            );
        } catch (UnexpectedPlayerException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            );
        } catch (UnknownStringException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such deck exists")
                    )
            );
        } catch (EmptyDeckException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new EmptyDeckException("Selected deck is empty")
                    )
            );
        }
    }

    @Override
    public void drawFromVisibleCards(VirtualClient sender, String deck, int position) {
        System.out.println("[CLIENT]: DrawFromVisibleCardsCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = ((GameController) playersToControllers.get(targetPlayer)).CONTROLLED_GAME;

        try {
            targetGame.getCurrentState().drawFrom(targetPlayer, deck, position);
        } catch (ForbiddenActionException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new ForbiddenActionException("Cannot draw a visible card in this state")
                    )
            );
        } catch (UnexpectedPlayerException e) {
            requestToClient(sender,
                    new ThrowExceptionCommand(
                            new UnexpectedPlayerException("Not this player's turn")
                    )
            );
        } catch (InvalidDeckPositionException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new InvalidDeckPositionException("Cannot understand which card to draw")
                    )
            );
        } catch (UnknownStringException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnknownStringException("No such placed cards exist")
                    )
            );
        } catch (EmptyDeckException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new EmptyDeckException("No card in selected slot")
                    )
            );
        }
    }

    @Override
    public void leaveGame(VirtualClient sender) {
        System.out.println("[CLIENT]: LeaveGameCommand received and being executed");

        InGamePlayer targetPlayer = (InGamePlayer) players.get(sender);
        Game targetGame = ((GameController) playersToControllers.get(targetPlayer)).CONTROLLED_GAME;

        targetPlayer.toggleActive();
        players.remove(sender);

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

        if (targetGame.getCurrentState().getCurrentPlayer() == null || targetGame.getCurrentState().getCurrentPlayer().equals(targetPlayer))
            targetGame.getCurrentState().playerDisconnected(targetPlayer);

        System.out.println("[SERVER]: sending ToggleActiveCommand to clients");

        for (var player : targetGame.getActivePlayers()) {
            VirtualClient targetClient = keyReverseLookup(players, player::equals);
            requestToClient(targetClient, new ToggleActiveCommand(player.getNickname()));
        }

        int activePlayers = targetGame.getActivePlayers().size();
        if (activePlayers == 1) {
            for (var player : targetGame.getActivePlayers())
                requestToClient(keyReverseLookup(players, player::equals), new PauseGameCommand());

            targetGame.setState(new AwaitingReconnectionState(targetGame));
            System.out.println("[SERVER]: Freezing " + targetGame.toString() + " game");
        } else if (activePlayers == 0) {
            ((AwaitingReconnectionState) targetGame.getCurrentState()).cancelTimerTask();
            for (var player : targetGame.getPlayers())
                playersToControllers.remove(player);
            lobbiesAndGames.remove(targetGame);
        }
    }

    @Override
    public void broadcastMessage(VirtualClient sender, String message) {
        System.out.println("[CLIENT]: BroadcastMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        System.out.println("[SERVER]: sending AddChatMessageCommand to clients");
        for (var inGamePlayer : ((GameController) playersToControllers.get(players.get(sender))).CONTROLLED_GAME.getPlayers())
            if (inGamePlayer.isActive())
                requestToClient(
                        keyReverseLookup(players, inGamePlayer::equals),
                        new AddChatMessageCommand(senderPlayer.getNickname(), message, false)
                );
    }

    @Override
    public void directMessage(VirtualClient sender, String receiverNickname, String message) {
        System.out.println("[CLIENT]: DirectMessageCommand received and being executed");

        InGamePlayer senderPlayer = (InGamePlayer) players.get(sender);

        Optional<Player> selectedPlayer = players.values().stream()
                .filter((player) -> player.getNickname().equals(receiverNickname))
                .findAny();

        //Truncating max message length
        message = message.substring(0, Math.min(message.length(), 150));

        if (selectedPlayer.isPresent()) {
            Player receiverPlayer = selectedPlayer.get();
            if (((GameController) playersToControllers.get(players.get(sender))).CONTROLLED_GAME
                    .equals(((GameController) playersToControllers.get(receiverPlayer)).CONTROLLED_GAME)) {
                if (((InGamePlayer) receiverPlayer).isActive()) {
                    System.out.println("[SERVER]: sending AddChatMessageCommand to sender and target client");
                    requestToClient(
                            sender,
                            new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                    );
                    requestToClient(
                            keyReverseLookup(players, receiverPlayer::equals),
                            new AddChatMessageCommand(senderPlayer.getNickname(), message, true)
                    );
                } else
                    requestToClient(
                            sender,
                            new ThrowExceptionCommand(
                                    new UnexpectedPlayerException("Nickname provided has no active player associated in this game")
                            )
                    );
            } else
                requestToClient(
                        sender,
                        new ThrowExceptionCommand(
                                new NotExistingPlayerException("Nickname provided has no associated player in this game")
                        )
                );
        } else
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new NotExistingPlayerException("Nickname provided has no associated player registered")
                    )
            );
    }
}
