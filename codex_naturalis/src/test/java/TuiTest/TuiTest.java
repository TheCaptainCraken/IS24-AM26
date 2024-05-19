package TuiTest;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.Tui;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class TuiTest {
    public static Tui tui;

    public static void main(String[] args) {
        LittleModel model = new LittleModel();
        Controller controller = new Controller();
        Tui tui = new Tui(model, controller);

//        for(int i = 97; i < 103; i++){
//            System.out.println("Card " + i);
//            String[] card = tui.createCardToPrint(model.getStartingCard(i, true));
//            for(int j = 0; j < 7; j++)
//                System.out.println(card[j]);
//        }
//
//        for (int i = 17; i < 97; i++) {
//            tui.printCard(i, true);
//        }
        //        for(int i = 17; i < 97; i++){
//            System.out.println("Card " + i);
//            tui.printCard(i, true);
//        }
//
//        for(int i = 1; i < 17; i++){
//            System.out.println("Card " + i);
//            tui.createObjectiveCardToPrint(i);
//            tui.showSecretObjectiveCard(i);
//        }
//
//        tui.showSecretObjectiveCard(1);
//        tui.showCommonObjectives(new Integer[]{1, 2});
//        tui.showSecretObjectiveCardsToChoose(new Integer[]{1, 2});
//
//
//        tui.printCard(17, true);

//    LittleModel model = new LittleModel();
//    Controller controller = new Controller();
//    TuiTest.tui = new Tui(model, controller);
//
//    for(int i = 97; i < 103; i++)
//        tui.showStartingCard(i);
//    }

    }
}





