package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.EnumMap;
import java.util.HashMap;

public class ClientPlayer {

    /**
     * This player's nickname
     */
    private final String OWN_NICKNAME;
    /**
     * This player's color
     */
    public final Color COLOR;

    /**
     * The resources owned by this player currently
     */
    private final EnumMap<Resource, Integer> OWNED_RESOURCES;

    /**
     * The field of this player
     */
    private final HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> PLACED_CARDS;

    /**
     * The points currently gained by this player
     */
    private int points;

    public ClientPlayer(String nickname, Color color){
        this.OWN_NICKNAME = nickname;
        this.COLOR = color;
        this.OWNED_RESOURCES = new EnumMap<>(Resource.class);
        this.PLACED_CARDS = new HashMap<>();
        this.points = 0;
    }
}
