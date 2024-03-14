package it.polimi.ingsw.gc12.ServerModel;

//TODO: add documentation comments

import java.util.Map;

public class CornersCondition implements PointsCondition {
    //FIXME: interfaces only allow public methods...
    public int numberOfTimesSatisfied(Card thisCard, InGamePlayer target) {
        //TODO: corners logic here, add try catches?
        int count = 0;
        GenericPair<Integer, Integer> cardCoords = target.getOwnField().entrySet().stream()
                .filter((entry) -> entry.getValue().equals(thisCard))
                .map(Map.Entry::getKey)
                .findAny().get();
        //FIXME: try catch here?

        for(int i = -1; i <= 1; i += 2){
            for(int j = -1; j <= 1; j += 2){
                if (target.getOwnField()
                        .containsKey(new GenericPair<Integer, Integer>(
                                cardCoords.getX() + i, cardCoords.getY() + j)
                        ))
                    count++;
            }
        }
        return count;
    }
}
