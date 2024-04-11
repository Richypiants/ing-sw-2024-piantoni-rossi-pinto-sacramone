package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Model.Conditions.PatternCondition;
import it.polimi.ingsw.gc12.Model.Conditions.PointsCondition;
import it.polimi.ingsw.gc12.Model.Game;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Resource;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldCardTest {

    @Test
    void awardPoints() {
        //TODO: da rifare!

        HashMap<GenericPair<Integer, Integer>, Resource> resource = new HashMap<>();
        resource.put(new GenericPair<>(0, 0), Resource.WOLF);
        resource.put(new GenericPair<>(1, 0), Resource.WOLF);
        resource.put(new GenericPair<>(0, 1), Resource.WOLF);
        resource.put(new GenericPair<>(1, 1), Resource.WOLF);
        Map<Side, Map<GenericPair<Integer, Integer>, Resource>> corner = new HashMap<>();
        corner.put(Side.FRONT, resource);
        corner.put(Side.BACK, resource);

        Triplet<Integer, Integer, Resource> T1 = new Triplet<>(0, 0, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T2 = new Triplet<>(0, -2, Resource.WOLF);
        Triplet<Integer, Integer, Resource> T3 = new Triplet<>(1, 1, Resource.WOLF);
        ArrayList<Triplet<Integer, Integer, Resource>> condition = new ArrayList<>();
        condition.add(T1);
        condition.add(T2);
        condition.add(T3);
        PointsCondition p = new PatternCondition(condition);

        GoldCard gold = new GoldCard(3, 1, corner, new EnumMap<>(Resource.class), p, null);

        Player p1 = new Player("SACRI");
        GameLobby lobby = new GameLobby(p1, 1);
        Game game = new Game(lobby);

        assertEquals(1, gold.awardPoints(game.getPlayers().getFirst()));

    }


}