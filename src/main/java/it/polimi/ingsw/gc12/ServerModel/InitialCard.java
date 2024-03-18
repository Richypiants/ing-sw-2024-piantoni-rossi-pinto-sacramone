package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Image;
import it.polimi.ingsw.gc12.Utilities.Resource;

import java.util.ArrayList;

/*
A template for an Initial card from the game's cards set
 */
public class InitialCard extends PlayableCard {

    /*
    Generates an Initial card from the given parameters
     */
    public InitialCard(int id, int pointsGranted, Image frontSprite, Image backSprite, Resource[][] corners,
                       ArrayList<Resource> centerBackResources) {
        super(id, pointsGranted, frontSprite, backSprite, corners, centerBackResources);
    }
}
