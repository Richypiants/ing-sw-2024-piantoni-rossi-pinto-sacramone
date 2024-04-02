package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.ServerModel.Cards.ObjectiveCard;
import it.polimi.ingsw.gc12.ServerModel.Cards.PlayableCard;
import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

public class ClientPlayer {

    /**
     * This player's color
     */
    public final Color COLOR = null;
    /**
     * The cards in this player's hand
     */
    private final ArrayList<PlayableCard> CARDS_IN_HAND; //TODO ---> va messo fuori perchè non lo vedo
    /**
     * The resources owned by this player currently
     */
    private final EnumMap<Resource, Integer> OWNED_RESOURCES;
    /**
     * The field of this player
     */
    private final HashMap<GenericPair<Integer, Integer>, GenericPair<PlayableCard, Side>> PLACED_CARDS;
    /**
     * This player's nickname
     */
    private String nickname;
    /**
     * The points currently gained by this player
     */
    private int points;
    /**
     * The secret Objective Card chosen by this player
     */
    private ObjectiveCard secretObjective; //TODO ---> va messo fuori perchè non lo vedo
}
