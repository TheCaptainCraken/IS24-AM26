package it.polimi.ingsw;

import it.polimi.ingsw.model.ColorPin;
import it.polimi.ingsw.model.Sign;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Player;

public class PlayerTest {
    @Test
    public void playerConstructor() {
        Player p1 = new Player("Pietro", ColorPin.BLUE, true);

        assert p1.getNickname().equals("Pietro");
        assert p1.getPin() == ColorPin.BLUE;
        assert p1.isFirst();
    }
    @Test
    public void playerPoints(){
        Player p1 = new Player("Pietro", ColorPin.BLUE, true);
        p1.updatePoints(29);
        assert p1.getPoints() == 29;

        Player p2 = new Player("Pietro", ColorPin.BLUE, true);
        p2.updatePoints(15);
        assert p2.getPoints() == 15;
        p2.updatePoints(15);
        assert p2.getPoints() == 29;

        Player p3 = new Player("Pietro", ColorPin.BLUE, true);
        p3.updatePoints(28);
        p3.updatePoints(1);
        assert p3.getPoints() == 29;

        Player p4 = new Player("Pietro", ColorPin.BLUE, true);
        p4.updatePoints(29);
        p4.updatePoints(1);
        assert p4.getPoints() == 29;

        Player p5 = new Player("Pietro", ColorPin.BLUE, true);
        p5.updatePoints(0);
        p5.updatePoints(0);
        assert p5.getPoints() == 0;
    }
    @Test
    public void playerSymbolCounter(){
        Player p1 = new Player("Pietro", ColorPin.BLUE, true);
        p1.addSymbolCounter(Sign.BUTTERFLY, 5);
        assert p1.getSymbolCounter().get(Sign.BUTTERFLY) == 5;
        p1.removeSymbolCounter(Sign.BUTTERFLY, 5);
        assert p1.getSymbolCounter().get(Sign.BUTTERFLY) == 0;
    }
}
