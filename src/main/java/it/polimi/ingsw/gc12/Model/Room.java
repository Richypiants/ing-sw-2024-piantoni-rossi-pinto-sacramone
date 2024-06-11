package it.polimi.ingsw.gc12.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a generic room where players can be grouped, such as a game lobby or a game.
 *
 * <p>This abstract class provides a basic structure for different types of rooms and implements
 * common functionality shared among them, such as managing players and providing room identification.</p>
 *
 * Instances of this class are identified by a unique UUID assigned when the room is created.
 *
 * <p>The room maintains a list of players who have already joined, allowing for operations such as
 * retrieving the list of players or determining the current number of players in the room.</p>
 *
 * <p>As an implementation of the {@link Serializable} interface, instances of this class can be serialized,
 * allowing for network transmission of room information.</p>
 */
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
