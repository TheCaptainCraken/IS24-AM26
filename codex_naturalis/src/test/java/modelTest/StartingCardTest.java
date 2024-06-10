package modelTest;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StartingCardTest {
    StartingCard s1;
    HashMap<Corner, Sign> map,map2;
    ArrayList<Sign> a1;


    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put(Corner.TOP_LEFT,Sign.BUTTERFLY);
        map.put(Corner.TOP_RIGHT,Sign.LEAF);
        map.put(Corner.BOTTOM_LEFT,Sign.WOLF);
        map.put(Corner.BOTTOM_RIGHT,Sign.MUSHROOM);
        map2 = new HashMap<>();
        map2.put(Corner.TOP_LEFT,Sign.QUILL);
        map2.put(Corner.TOP_RIGHT,Sign.EMPTY);
        map2.put(Corner.BOTTOM_RIGHT,null);
        map2.put(Corner.BOTTOM_LEFT,Sign.BUTTERFLY);
        a1 = new ArrayList<>();
        a1.add(Sign.LEAF);

    }

    @Test
    void NormalTest(){
        s1 = new StartingCard(3, Kingdom.FUNGI,map,map2,a1);
        Assertions.assertEquals(s1.getBacksideCorners(),map2);
        Assertions.assertEquals(s1.getBonusResources(),a1);
    }
}
