package it.polimi.ingsw.gc12.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Room implements Serializable {

    private final UUID ROOM_UUID;
    /**
     * The list of players who have already joined this lobby.
     */
    protected final List<Player> LIST_OF_PLAYERS;

    protected Room(UUID roomUUID, List<Player> listOfPlayers) {
        this.ROOM_UUID = roomUUID;
        LIST_OF_PLAYERS = listOfPlayers;
    }

    public UUID getRoomUUID() {
        return ROOM_UUID;
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
}
