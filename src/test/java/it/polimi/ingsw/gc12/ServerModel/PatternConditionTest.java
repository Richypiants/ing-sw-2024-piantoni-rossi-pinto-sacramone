package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

//TODO: think about all possible fails and tests

class PatternConditionTest {

    @Test
    void patternWithNoTiles() {
        PatternCondition p = new PatternCondition(
                new ArrayList<Triplet<Integer, Integer, Resource>>()
        );

        //TODO: come back here after implementing ObjectiveCard and InGamePlayer
        assertThrows(p.numberOfTimesSatisfied(
                        //TODO: add exception here
                        new ObjectiveCard(0, 0, null, null, null),
                        new InGamePlayer("tester")
                )
        );
    }

    @Test
    void patternWithOnlyOneTile() {
        //TODO: come up with a good example
        PatternCondition p = new PatternCondition(
                new ArrayList<Triplet<Integer, Integer, Resource>>(x)
        );

        //TODO: come back here after implementing ObjectiveCard and InGamePlayer
        assertX(p.numberOfTimesSatisfied(
                        new ObjectiveCard(0, 0, null, null, null),
                        new InGamePlayer("tester")
                )
        );
    }

    @Test
    void patternWithMoreTiles() {
        //TODO: come up with a good example
        PatternCondition p = new PatternCondition(
                new ArrayList<Triplet<Integer, Integer, Resource>>()
        );

        //TODO: come back here after implementing ObjectiveCard and InGamePlayer
        assertX(p.numberOfTimesSatisfied(
                        new ObjectiveCard(0, 0, null, null, null),
                        new InGamePlayer("tester")
                )
        );
    }

}