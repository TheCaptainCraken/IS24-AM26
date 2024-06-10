package modelTest;

import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.ObjectiveType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.ObjectiveCard;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectiveCardTest {
    ObjectiveCard o1;

    @Test
    void NormalTest(){
        o1 = new ObjectiveCard(1, Kingdom.PLANT, ObjectiveType.STAIR,2);
        Assertions.assertEquals(o1.getType(),ObjectiveType.STAIR);
        Assertions.assertEquals(o1.getMultiplier(),2);

    }
}
