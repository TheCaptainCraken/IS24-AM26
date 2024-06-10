package modelTest;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNull;

public class PlayedCardTest {
    Point p;
    Point p2;
    PlayableCard test;
    HashMap<Corner, PlayedCard> map1;
    HashMap<Corner, PlayedCard> map;

    @BeforeEach
    void setUp() {
        p = new Point(1, 2);
        p2 = new Point(1, 2);
        test = new ResourceCard(1, Kingdom.ANIMAL, null, 1);
        map1 = new HashMap<Corner, PlayedCard>();
        map = new HashMap<Corner, PlayedCard>();
    }

    @Test
    public void NullHashMapTest() {
        PlayedCard t1 = new PlayedCard(test, map, true, 1, p);
        Assertions.assertNull(t1.getAttached(Corner.TOP_LEFT));
        Assertions.assertNull(t1.getAttached(Corner.TOP_RIGHT));
        Assertions.assertNull(t1.getAttached(Corner.BOTTOM_LEFT));
        Assertions.assertNull(t1.getAttached(Corner.BOTTOM_RIGHT));
    }

    @Test
    public void NormalCaseTest() {
        PlayedCard t1 = new PlayedCard(test, map, true, 1, p);
        map1.put(Corner.BOTTOM_LEFT, t1);
        PlayedCard t2 = new PlayedCard(test, map1, false, 2, p2);
        assert (t2.getAttached(Corner.BOTTOM_LEFT).equals(t1));
        assert (t1.getAttached(Corner.TOP_RIGHT).equals(t2));
        assert (!t2.isFacingUp());
        assert (!t2.isFlagCountedForObjective());
        t2.flagWasCountedForObjective();
        assert (t2.isFlagCountedForObjective());
        assert (t2.getCard().equals(test));
        assert (t2.getTurnOfPositioning() == 2);
        assert (t2.getAttached(Corner.BOTTOM_LEFT).equals(t1));
        assert(t2.getPosition().equals(p));
    }

}
