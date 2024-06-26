package modelTest;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlayerTest {
    //test of constructor
    private static ResourceCard card1;
    private static ResourceCard card2;
    private static ResourceCard card3;

    @BeforeAll
    public static void setup(){
        HashMap<Corner, Sign> corners = new HashMap<>();

        card1 = new ResourceCard(17, Kingdom.ANIMAL, corners, 0);
        card2 = new ResourceCard(11, Kingdom.FUNGI, corners, 1);
        card3 = new ResourceCard(9, Kingdom.INSECT, corners, 1);
    }
    @Test
    public void playerConstructor() {
        Player p1 = new Player("Pietro");
        assert p1.getName().equals("Pietro");
    }

    @Test
    @DisplayName("test of points")
    public void UpdatePointsCorrectly(){
        Player p1 = new Player("Pietro");
        p1.addPoints(29);
        assert p1.getPoints() == 29;

        Player p2 = new Player("Pietro");
        p2.addPoints(15);
        assert p2.getPoints() == 15;
        p2.addPoints(15);
        assert p2.getPoints() == 29;

        Player p3 = new Player("Pietro");
        p3.addPoints(28);
        p3.addPoints(1);
        assert p3.getPoints() == 29;

        Player p4 = new Player("Pietro");
        p4.addPoints(29);
        p4.addPoints(1);
        assert p4.getPoints() == 29;

        Player p5 = new Player("Pietro");
        p5.addPoints(0);
        p5.addPoints(0);
        assert p5.getPoints() == 0;

        p5.addObjectivePoints(30);
        Assertions.assertEquals(p5.getObjectivePoints(), 30);
    }
    @Test
    @DisplayName("resources update")
    public void checkRegularUpdateOfResources(){
        Player p1 = new Player("Pietro");
        p1.addResource(Sign.BUTTERFLY, 5);
        assert p1.getResources().get(Sign.BUTTERFLY) == 5;
        p1.removeResources(Sign.BUTTERFLY, 5);
        assert p1.getResources().get(Sign.BUTTERFLY) == 0;
        p1.addResource(Sign.MUSHROOM, 5);
        assert p1.getResources().get(Sign.MUSHROOM) == 5;
        p1.removeResources(Sign.MUSHROOM, 4);
        assert p1.getResources().get(Sign.MUSHROOM) == 1;
    }
    @Test
    @DisplayName("test of player's hand")
    public void checkTakeCard(){
        Player p1 = new Player("Pietro");
        p1.takeCard(card1);
        p1.takeCard(card2);
        p1.takeCard(card3);

        Assertions.assertEquals(p1.getHand()[0], card1);
        Assertions.assertEquals(p1.getHand()[1], card2);
        Assertions.assertEquals(p1.getHand()[2], card3);
    }
    @Test
    @DisplayName("remove cards")
    public void RemoveCardCorrectly(){
        Player p1 = new Player("Pietro");
        p1.takeCard(card1);
        p1.takeCard(card2);
        p1.takeCard(card3);

        p1.giveCard(card1);
        Assertions.assertNull(p1.getHand()[0]);
        p1.takeCard(card1);
        Assertions.assertEquals(p1.getHand()[0], card1);

        p1.giveCard(card3);
        Assertions.assertNull(p1.getHand()[2]);
        p1.takeCard(card3);
        Assertions.assertEquals(p1.getHand()[2], card3);
    }
}
