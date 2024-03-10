package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.HashMap;

public class Field {
    private HashMap<Pair<Integer, Integer>, PlayableCard> field = new HashMap< Pair<Integer, Integer>, PlayableCard >();
    private ArrayList<Pair<Integer, Integer>> openCorners = new ArrayList< Pair<Integer, Integer> >();

    protected Field() {
        // Field initialization logic
    }

    protected Field(Field field){
    }

    protected void addCard(Pair<Integer, Integer> position, PlayableCard card) {
        field.put(position, card);
        // Further logic to handle open corners if necessary
    }

    protected ArrayList< Pair<Integer, Integer> > getOpenCorners(){
        return new ArrayList<Pair<Integer, Integer>>(openCorners);
    }


}
