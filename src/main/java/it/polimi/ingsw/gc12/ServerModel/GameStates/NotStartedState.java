package it.polimi.ingsw.gc12.ServerModel.GameStates;

import it.polimi.ingsw.gc12.ServerController.Controller;
import it.polimi.ingsw.gc12.ServerModel.GameLobby;
import it.polimi.ingsw.gc12.ServerModel.Player;

import java.util.UUID;

public class NotStartedState extends GameState {

    //FIXME: not that good...
    public NotStartedState() {
        super(null, -1, -1);
    }

    public static void createLobby(Player player, int maxPlayers) {
        //TODO: exceptions? invalidMaxPlayers, ...
        // e tutte le varie eccezioni se il player è in Game e non in lobby?
        // si potrebbe risolvere mettendo un GameState "NotStartedState o IdleState"...

        //FIXME: fixare anche il metodo del model nell'UML oltre a invertire ordine parametri in GameLobby
        //GameLobby lobby = new GameLobby(player, maxPlayers);
        UUID lobbyUUID;

        do {
            lobbyUUID = UUID.randomUUID();
        } while (!Controller.lobbiesAndGames.containsKey(lobbyUUID));

        //lobbiesAndGames.put(lobby.UUID, lobby);
        //playersToLobbiesAndGames.put(player, lobby);
    }

    @Override
    public void setNickname(Player target, String nickname) {
        if (Controller.playersToLobbiesAndGames.containsValue(nickname))
            //throw new AlreadyExistingPlayerException();

            target.setNickname(nickname);
    }

    @Override
    public void joinLobby(Player player, UUID lobbyUUID) {
        //TODO: exceptions? lobby non trovata, lobby piena, ...

        GameLobby lobby = Controller.lobbiesAndGames.get(lobbyUUID);
        lobby.addPlayer(player);
        Controller.playersToLobbiesAndGames.put(player, lobby);
    }

    @Override
    public void leaveLobby(Player player) {
        //TODO: exceptions? not in lobby, ...

        GameLobby lobby = Controller.playersToLobbiesAndGames.get(player);

        if (lobby.getListOfPlayers().size() == 1) {
            //Controller.lobbiesAndGames.remove(Controller.lobbiesAndGames.entrySet().get(lobby));
            return;
        }

        lobby.removePlayer(player);
        Controller.playersToLobbiesAndGames.remove(player);
    }

    @Override
    public void transition() {
        super.transition();

        GAME.setState(new ChooseInitialCardsState(GAME));
    }
}
