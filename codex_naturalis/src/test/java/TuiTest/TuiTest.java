package TuiTest;

import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.Tui;
import it.polimi.ingsw.model.*;

import java.util.HashMap;

public class TuiTest {

public static void main(String[] args) {
        LittleModel model = new LittleModel();
        Tui tui = new Tui(model);

        tui.showSecretObjectiveCard(1);
        tui.showCommonObjectives(new Integer[]{1, 2});
        tui.showSecretObjectiveCardsToChoose(new Integer[]{1, 2});


        tui.printCard(17);
    }
}
