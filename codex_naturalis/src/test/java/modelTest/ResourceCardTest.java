package modelTest;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceCardTest {
    ResourceCard r1,r2;
    HashMap<Corner, Sign> map;


    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put(Corner.TOP_LEFT,Sign.BUTTERFLY);
        map.put(Corner.TOP_RIGHT,Sign.LEAF);
        map.put(Corner.BOTTOM_LEFT,Sign.WOLF);
        map.put(Corner.BOTTOM_RIGHT,Sign.MUSHROOM);
    }

    @Test
    public void NormalTest(){
        r1 = new ResourceCard(1, Kingdom.ANIMAL,map,1);
        Assertions.assertEquals(r1.getId(),1);
        Assertions.assertEquals(r1.getKingdom(),Kingdom.ANIMAL);
        Assertions.assertEquals(r1.getCorners(),map);
        Assertions.assertEquals(r1.getId(),1);
        Assertions.assertEquals(r1.getPoints(),1);
    }
}
