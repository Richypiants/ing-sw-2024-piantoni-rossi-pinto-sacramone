package it.polimi.ingsw.gc12.ServerModel;

//TODO: add documentation comments

public class CornersCondition implements PointsCondition {
    //FIXME: interfaces only allow public methods...
    public int numberOfTimesSatisfied(InGamePlayer target) {
        //TODO: corners logic here, add try catches?
        int count = 0;

        for(int i = -1; i <= 1; i += 2){
            for(int j = -1; j <= 1; j += 2){
                if(target.getOwnField().containsKey(new Pair<Integer, Integer>(i, j)))
                    count++;
            }
        }
        return count;
    }
}
