package it.polimi.ingsw.gc12.ServerModel;

// This interface declares a standard method to compute conditions to assign points
// These condition will be detailed in subclasses after being read from JSON
public interface PointsCondition {
    // Method to compute how many times is a condition satisfied
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer of);
}
