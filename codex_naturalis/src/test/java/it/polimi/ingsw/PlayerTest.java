package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Sign;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Player;

public class PlayerTest {
    @Test
    public void playerConstructor() {
        Player p1 = new Player("Pietro", Color.BLUE);
        assert p1.getName().equals("Pietro");
        assert p1.getColor() == Color.BLUE;
    }
    @Test
    public void playerPoints(){
        Player p1 = new Player("Pietro", Color.BLUE);
        p1.updatePoints(29);
        assert p1.getPoints() == 29;

        Player p2 = new Player("Pietro", Color.BLUE);
        p2.updatePoints(15);
        assert p2.getPoints() == 15;
        p2.updatePoints(15);
        assert p2.getPoints() == 29;

        Player p3 = new Player("Pietro", Color.BLUE);
        p3.updatePoints(28);
        p3.updatePoints(1);
        assert p3.getPoints() == 29;

        Player p4 = new Player("Pietro", Color.BLUE);
        p4.updatePoints(29);
        p4.updatePoints(1);
        assert p4.getPoints() == 29;

        Player p5 = new Player("Pietro", Color.BLUE);
        p5.updatePoints(0);
        p5.updatePoints(0);
        assert p5.getPoints() == 0;
    }
    @Test
    public void playerSymbolCounter(){
        Player p1 = new Player("Pietro", Color.BLUE);
        p1.addResource(Sign.BUTTERFLY, 5);
        assert p1.getResources().get(Sign.BUTTERFLY) == 5;
        p1.removeResources(Sign.BUTTERFLY, 5);
        assert p1.getResources().get(Sign.BUTTERFLY) == 0;
    }
}
