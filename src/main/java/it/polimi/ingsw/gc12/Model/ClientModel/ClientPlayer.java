package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.io.Serializable;
import java.util.*;

public class ClientPlayer extends Player implements Serializable {

    /**
     * The resources owned by this player currently
     */
    private EnumMap<Resource, Integer> ownedResources;
    /**
     * The field of this player
     */
    private final LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<ClientCard, Side>> PLACED_CARDS;
    /**
     * The positions where this player can currently place new cards
     */
    private List<GenericPair<Integer, Integer>> openCorners;
    /**
     * The points currently gained by this player
     */
    private int points;
    /**
     *
     */
    private boolean isActive = true; //TODO: implement activity management

    public ClientPlayer(Player player){
        super(player);
        this.ownedResources = new EnumMap<>(Resource.class);
        this.PLACED_CARDS = new LinkedHashMap<>();
        this.openCorners = new ArrayList<>();
        this.points = 0;
    }

    public EnumMap<Resource, Integer> getOwnedResources(){
        return ownedResources; //TODO: copia?
    }

    public void setOwnedResources(EnumMap<Resource, Integer> ownedResources){
        this.ownedResources = ownedResources;
    }

    public LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<ClientCard, Side>> getPlacedCards(){
        return PLACED_CARDS; //TODO: copia?
    }

    public void placeCard(GenericPair<Integer, Integer> coordinates, ClientCard card, Side playedSide){
        PLACED_CARDS.put(coordinates, new GenericPair<>(card, playedSide));
    }

    public List<GenericPair<Integer, Integer>> getOpenCorners() {
        return openCorners; //TODO: copia?
    }

    public void setOpenCorners(List<GenericPair<Integer, Integer>> openCorners) {
        this.openCorners = openCorners;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public boolean isActive(){
        return isActive;
    }

    public void toggleActive(){
        isActive = !isActive;
    }
}
