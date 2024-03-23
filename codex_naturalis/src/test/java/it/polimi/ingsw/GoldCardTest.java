package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoldCardTest {
    GoldCard g1;
    HashMap<Corner, Sign> map;
    HashMap<Sign,Integer> map1;


    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put(Corner.TOP_LEFT,Sign.BUTTERFLY);
        map.put(Corner.TOP_RIGHT,Sign.LEAF);
        map.put(Corner.BOTTOM_LEFT,Sign.WOLF);
        map.put(Corner.BOTTOM_RIGHT,Sign.MUSHROOM);
        map1 = new HashMap<>();
        map1.put(Sign.BUTTERFLY,2);
        map1.put(Sign.LEAF,1);

    }
    @Test
    void NormalTest(){
        g1 = new GoldCard(2, Kingdom.FUNGI,map,0,map1);
        assertEquals(g1.getRequirements(),map1);

    }

}
