package it.polimi.ingsw.gc12.ServerModel;

public class ResourceCard extends PlayableCard {
    public ResourceCard(int id, int points, Image frontSprite, Image backSprite, Resource[] cornerResources, boolean[] cornerValidity) {
        super(id, points, frontSprite, backSprite, cornerResources, cornerValidity);
        // Further initialization if necessary
    }
}
