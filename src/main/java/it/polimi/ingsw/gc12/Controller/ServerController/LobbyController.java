package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.StartGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;

import java.util.Arrays;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class LobbyController extends ServerController {

    //TODO: implement this here
    //private final UUID lobbyUUID;
    //FIXME: this one here should be private...
    public final Lobby CONTROLLED_LOBBY;

    public LobbyController(Lobby controlledLobby) {
        this.CONTROLLED_LOBBY = controlledLobby;
    }

    @Override
    public void pickColor(NetworkSession sender, Color color) {
        System.out.println("[CLIENT]: PickColorCommand received and being executed");

        if (Arrays.stream(Color.values()).noneMatch(color::equals))
            sender.getListener().notified(
                    new ThrowExceptionCommand(new IllegalArgumentException("The specified color doesn't exist"))
            );

        Player target = activePlayers.get(sender);

        try {
            CONTROLLED_LOBBY.assignColor(target, color);
        } catch (UnavailableColorException e) {
            sender.getListener().notified(
                    new ThrowExceptionCommand(
                            new UnavailableColorException("The specified color is not available for this lobby")
                    )
            );
            return;
        }

        UUID lobbyUUID = keyReverseLookup(model.LOBBY_CONTROLLERS, this::equals);

        //TODO: startGame()... && add synchronization
        if (CONTROLLED_LOBBY.getAvailableColors().size() <= 4 - CONTROLLED_LOBBY.getMaxPlayers()) {
            Game newGame = new Game(CONTROLLED_LOBBY);
            GameController controller = new GameController(newGame);

            model.GAME_CONTROLLERS.put(lobbyUUID, controller);
            model.LOBBY_CONTROLLERS.remove(lobbyUUID);

            System.out.println("[SERVER]: sending StartGameCommand to clients starting game");
            //TODO: estrarre la logica di evoluzione dei player da Game (altrimenti, fixare i get) E SINCRONIZZAREEEE
            for (var player : CONTROLLED_LOBBY.getPlayers()) {
                NetworkSession targetClient = keyReverseLookup(activePlayers, player::equals);
                InGamePlayer targetInGamePlayer = newGame.getPlayers().stream()
                        .filter((inGamePlayer) -> inGamePlayer.getNickname().equals(player.getNickname()))
                        .findFirst()
                        .orElseThrow(); //TODO: strano... gestire?

                activePlayers.put(targetClient, targetInGamePlayer);
                targetClient.setController(controller);

                newGame.addListener(targetClient.getListener());
                targetInGamePlayer.addListener(targetClient.getListener());
                targetClient.getListener().notified(new StartGameCommand(lobbyUUID, newGame.generateDTO(targetInGamePlayer)));

                //FIXME: should clients inform that they are ready before? (ready() method call?)
                //Calls to game creation, generateInitialCards ...
            }
            controller.getCurrentState().transition();

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & Lobby?
            while (CONTROLLED_LOBBY.getPlayersNumber() > 0) {
                CONTROLLED_LOBBY.removePlayer(CONTROLLED_LOBBY.getPlayers().getFirst());
            }
        }

        for (var client : activePlayers.keySet())
            if (!(activePlayers.get(client) instanceof InGamePlayer))
                client.getListener().notified(
                        new UpdateLobbyCommand(lobbyUUID, CONTROLLED_LOBBY)
                ); //updateLobby();
    }

    @Override
    public void leaveLobby(NetworkSession sender, boolean isInactive) {
        System.out.println("[CLIENT]: LeaveLobbyCommand received and being executed");

        Player target = activePlayers.get(sender);

        UUID lobbyUUID = keyReverseLookup(model.LOBBY_CONTROLLERS, this::equals);
        //Assuming that lobby is contained (thus maps are coherent): check with synchronization that this
        // invariant holds

        CONTROLLED_LOBBY.removePlayer(target);
        sender.setController(ConnectionController.getInstance());

        if (CONTROLLED_LOBBY.getPlayers().isEmpty()) {
            model.LOBBY_CONTROLLERS.remove(lobbyUUID);
        }

        if (isInactive){
            activePlayers.remove(sender);
        }

        System.out.println("[SERVER]: sending UpdateLobbiesCommand to clients");

        for (var client : activePlayers.keySet())
            if (!(activePlayers.get(client) instanceof InGamePlayer))
                client.getListener().notified(new UpdateLobbyCommand(lobbyUUID, CONTROLLED_LOBBY)); // updateLobby();
    }
}
