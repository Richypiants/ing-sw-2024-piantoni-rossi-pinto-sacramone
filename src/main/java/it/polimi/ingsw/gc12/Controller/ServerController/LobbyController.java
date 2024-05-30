package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.StartGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ThrowExceptionCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.UpdateLobbyCommand;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.InGamePlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Network.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;

import java.util.Arrays;
import java.util.UUID;

import static it.polimi.ingsw.gc12.Utilities.Commons.keyReverseLookup;

public class LobbyController extends ServerController {

    //TODO: implement this here
    //private final UUID lobbyUUID;
    //FIXME: this one here should be private...
    protected final GameLobby CONTROLLED_LOBBY;

    public LobbyController(GameLobby controlledLobby) {
        this.CONTROLLED_LOBBY = controlledLobby;
    }

    @Override
    public void pickColor(VirtualClient sender, Color color) {
        System.out.println("[CLIENT]: PickColorCommand received and being executed");

        if (Arrays.stream(Color.values()).noneMatch(color::equals))
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(new IllegalArgumentException("The specified color doesn't exist"))
            );

        Player target = players.get(sender);

        try {
            CONTROLLED_LOBBY.assignColor(target, color);
        } catch (UnavailableColorException e) {
            requestToClient(
                    sender,
                    new ThrowExceptionCommand(
                            new UnavailableColorException("The specified color is not available for this lobby")
                    )
            );
            return;
        }

        UUID lobbyUUID = keyReverseLookup(lobbiesAndGames, CONTROLLED_LOBBY::equals);

        //TODO: startGame()... && add synchronization
        if (CONTROLLED_LOBBY.getAvailableColors().size() <= 4 - CONTROLLED_LOBBY.getMaxPlayers()) {
            Game newGame = new Game(CONTROLLED_LOBBY);
            GameController controller = new GameController(newGame);

            lobbiesAndGames.put(lobbyUUID, newGame);

            System.out.println("[SERVER]: sending StartGameCommand to clients starting game");
            //TODO: estrarre la logica di evoluzione dei player da Game (altrimenti, fixare i get) E SINCRONIZZAREEEE
            for (var player : CONTROLLED_LOBBY.getPlayers()) {
                VirtualClient targetClient = keyReverseLookup(players, player::equals);
                InGamePlayer targetInGamePlayer = newGame.getPlayers().stream()
                        .filter((inGamePlayer) -> inGamePlayer.getNickname().equals(player.getNickname()))
                        .findFirst()
                        .orElseThrow(); //TODO: strano... gestire?

                players.put(targetClient, targetInGamePlayer);
                playersToControllers.remove(target);
                playersToControllers.put(targetInGamePlayer, controller);

                requestToClient(targetClient, new StartGameCommand(lobbyUUID, newGame.generateDTO(targetInGamePlayer)));

                //FIXME: should clients inform that they are ready before? (ready() method call?)
                //Calls to game creation, generateInitialCards ...
            }
            newGame.getCurrentState().transition();

            //FIXME: a better solution? or does this get fixed by fixing constructors for Game & GameLobby?
            while (CONTROLLED_LOBBY.getPlayersNumber() > 0) {
                CONTROLLED_LOBBY.removePlayer(CONTROLLED_LOBBY.getPlayers().getFirst());
            }
        }

        for (var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(
                        client,
                        new UpdateLobbyCommand(lobbyUUID, CONTROLLED_LOBBY)
                ); //updateLobby();
    }

    @Override
    public void leaveLobby(VirtualClient sender, boolean isInactive) {
        System.out.println("[CLIENT]: LeaveLobbyCommand received and being executed");

        Player target = players.get(sender);

        GameLobby lobby = CONTROLLED_LOBBY;
        UUID lobbyUUID = keyReverseLookup(lobbiesAndGames, lobby::equals);
        //Assuming that lobby is contained (thus maps are coherent): check with synchronization that this
        // invariant holds

        lobby.removePlayer(target);
        playersToControllers.remove(target);

        if (lobby.getPlayers().isEmpty()) {
            lobbiesAndGames.remove(lobbyUUID);
        }

        if (isInactive){
            disconnectionRoutine(sender);
            players.remove(sender);
        }


        System.out.println("[SERVER]: sending UpdateLobbiesCommand to clients");

        for (var client : players.keySet())
            if (!(players.get(client) instanceof InGamePlayer))
                requestToClient(client, new UpdateLobbyCommand(lobbyUUID, lobby)); // updateLobby();
    }
}
