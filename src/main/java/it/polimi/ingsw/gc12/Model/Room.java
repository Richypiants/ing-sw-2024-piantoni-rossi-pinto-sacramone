package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Listeners.Listenable;
import it.polimi.ingsw.gc12.Listeners.Listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Room implements Serializable, Listenable {

    /**
     * The list of players who have already joined this lobby.
     */
    protected final List<Player> LIST_OF_PLAYERS;
    //FIXME: don't want to serialize these, how to remove from serialized GameLobby? is transient correct?
    private transient final List<Listener> ROOM_LISTENERS;

    protected Room(List<Player> listOfPlayers) {
        ROOM_LISTENERS = new ArrayList<>();
        LIST_OF_PLAYERS = listOfPlayers;
    }

    /**
     * Returns the list of players currently in the lobby.
     *
     * @return A copy of the list of players in the lobby.
     */
    public ArrayList<? extends Player> getPlayers() {
        return new ArrayList<>(LIST_OF_PLAYERS);
    }

    /**
     * Returns the number of players currently in the lobby.
     *
     * @return The number of players in the lobby.
     */
    public int getPlayersNumber() {
        return LIST_OF_PLAYERS.size();
    }

    @Override
    public void addListener(Listener listener) {
        ROOM_LISTENERS.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        ROOM_LISTENERS.remove(listener);
    }

    @Override
    public void notifyListeners() {
        for (var listener : ROOM_LISTENERS)
            listener.notified();
    }
}
