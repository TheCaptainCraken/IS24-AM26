package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashMap;

public class PlayedCardTest {
    Point p;
    Point p2;
    PlayableCard test;
    HashMap<Corner,PlayedCard> map1 ;
    HashMap<Corner,PlayedCard> map ;

    @BeforeEach
    void setUp() {
        p = new Point(1,2);
        p2 = new Point(1,2);
        test = new ResourceCard(1,Kingdom.ANIMAL,1);
        map1 = new HashMap<Corner,PlayedCard>();
        map = new HashMap<Corner,PlayedCard>();
    }


    @Test
    public void NullHashMapTest(){
        PlayedCard t1 = new PlayedCard(test,map,true,1,p);
        //assert();

    }
    @Test
    public void NormalCaseTest(){
        PlayedCard t1 = new PlayedCard(test,map,true,1,p);
        map1.put(Corner.BOTTOM_LEFT,t1);
        PlayedCard t2 = new PlayedCard(test,map1,false,2,p2);
        assert(t2.getAttachmentCorners().containsKey(Corner.BOTTOM_LEFT));
        t2.attachCard(Corner.BOTTOM_RIGHT,t1);
        assert(t2.getAttachmentCorners().containsKey(Corner.BOTTOM_RIGHT));
        assert(!t2.isFacingUp());
        assert(!t2.isFlagCountedForObjective());
        t2.flagWasCountedForObjective();
        assert (t2.isFlagCountedForObjective());
        assert(t2.getCard().equals(test));
        assert(t2.getTurnOfPositioning() == 2);
        assert(t2.getAttached(Corner.BOTTOM_LEFT).equals(t1));
    }



}
