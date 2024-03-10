package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

public class PlayableCard extends Card {
    public final Corner[][] CORNERS = new Corner[2][4]; // Front and back, with four corners each
    public final ArrayList<Resource> CENTER_BACK_RESOURCE = new ArrayList<>();

    public PlayableCard(int id, int points, Image frontSprite, Image backSprite, Resource[] resources, boolean[] validities) {
        super(id, points, frontSprite, backSprite);
        // Initialize CORNERS and CENTER_BACK_RESOURCE based on parameters
    }
}
