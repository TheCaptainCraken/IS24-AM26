package TuiTest;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.view.model.LittleModel;
import it.polimi.ingsw.view.TUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

//Little model senza codex_naturals
public class TuiTest {
    private static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";
    private static TUI tui;
    private static LittleModel model;

    @BeforeEach
    public void setUp() {
        model = new LittleModel
                (basePath + "startingCardsDeck.json", basePath + "objectiveCardsDeck.json",
                        basePath + "resourceCardsDeck.json", basePath + "goldCardsDeck.json");
        tui = new TUI(model, null);
    }

    @Test
    public void startingCardsTrue() {
        for (int i = 97; i < 103; i++) {
            System.out.println("Card " + i);
            String[] card = tui.createCardToPrint(model.getStartingCard(i, true));
            for (int j = 0; j < 8; j++)
                System.out.println(card[j]);
        }
    }
    @Test
    public void startingCardsFalse(){
        for (int i = 97; i < 103; i++) {
            System.out.println("Card " + i);
            String[] card = tui.createCardToPrint(model.getStartingCard(i, false));
            for (int j = 0; j < 8; j++)
                System.out.println(card[j]);
        }
    }

    @Test
    public void resourceAndGoldCards() {
        ArrayList<String[]> cards = new ArrayList<>();

        for(int i = 18; i< 97; i++) {
            cards.add(tui.createCardToPrint(model.getCard(i, true)));
        }

        tui.printCardArray(cards);
        }
    @Test
    public void resourceAndGoldCardsFalse() {
        ArrayList<String[]> cards = new ArrayList<>();

        for(int i = 17; i< 97; i++) {
            cards.add(tui.createCardToPrint(model.getCard(i, false)));
        }

        tui.printCardArray(cards);
    }

    @Test
    public void goldCards() {
        ArrayList<String[]> cards = new ArrayList<>();
        for (int i = 57; i < 97; i++) {
            cards = new ArrayList<>();
            System.out.println("Card " + i);
            cards.add(tui.createCardToPrint(model.getCard(i, true)));
            tui.printCardArray(cards);
        }
    }

    @Test
    public void allCards() {
        ArrayList<String[]> cards = new ArrayList<>();
        for (int i = 17; i < 103; i++) {
            cards.add(tui.createCardToPrint(model.getCard(i, true)));
            tui.printCardArray(cards);
        }
    }

    @Test
    public void allObjectiveCards() {
        for (int i = 1; i < 17; i++) {
            System.out.println("Card " + i);
            tui.showSecretObjectiveCard(i);
        }
    }

    @Test
    public void showTableOfPlayer() {
        HashMap<String, Color> users = new HashMap<>();
        users.put("pippo", Color.BLUE);
        model.updateUsers(users);
        model.updatePlaceCard("pippo", 97, new Point(0, 0), false, 0);
        model.updatePlaceCard("pippo", 17, new Point(-1, 0), false, 1);
        model.updatePlaceCard("pippo", 18, new Point(-1, -1), false, 2);
        model.updatePlaceCard("pippo", 19, new Point(1, 0), false, 3);
        model.updatePlaceCard("pippo", 20, new Point(1, 1), false, 4);
        model.updatePlaceCard("pippo", 21, new Point(-2, 0), false, 5);
        model.updatePlaceCard("pippo", 22, new Point(-2, -1), false, 6);

        tui.showTableOfPlayer("pippo");
    }

    @Test
    public void showTableOfPlayer1() {
        HashMap<String, Color> users = new HashMap<>();
        users.put("pippo", Color.BLUE);
        model.updateUsers(users);
        model.updatePlaceCard("pippo", 97, new Point(0, 0), false, 0);
        model.updatePlaceCard("pippo", 17, new Point(-1, 0), false, 1);
        model.updatePlaceCard("pippo", 18, new Point(-1, -1), false, 2);
        model.updatePlaceCard("pippo", 19, new Point(1, 0), false, 3);
        model.updatePlaceCard("pippo", 20, new Point(1, 1), false, 4);
        model.updatePlaceCard("pippo", 21, new Point(-2, 0), false, 5);
        model.updatePlaceCard("pippo", 22, new Point(-2, -1), false, 6);
        model.updatePlaceCard("pippo", 23, new Point(-1, 1), false, 7);
        model.updatePlaceCard("pippo", 24, new Point(0, 1), false, 8);
        model.updatePlaceCard("pippo", 25, new Point(0, -1), false, 9);

        tui.showTableOfPlayer("pippo");
    }

    @Test
    public void showTableOfPlayer2() {
        HashMap<String, Color> users = new HashMap<>();
        users.put("pippo", Color.BLUE);
        model.updateUsers(users);

        model.updatePlaceCard("pippo", 97, new Point(0, 0), true, 0);
        model.updatePlaceCard("pippo", 17, new Point(-1, 0), true, 1);
        model.updatePlaceCard("pippo", 18, new Point(-1, -1), true, 2);
        model.updatePlaceCard("pippo", 19, new Point(1, 0), true, 3);
        model.updatePlaceCard("pippo", 20, new Point(1, 1), true, 4);
        model.updatePlaceCard("pippo", 21, new Point(-2, 0), true, 5);
        model.updatePlaceCard("pippo", 22, new Point(-2, -1), true, 6);
        model.updatePlaceCard("pippo", 23, new Point(-1, 1), true, 7);
        model.updatePlaceCard("pippo", 24, new Point(0, 1), true, 8);
        model.updatePlaceCard("pippo", 25, new Point(0, -1), true, 9);
        model.updatePlaceCard("pippo", 26, new Point(1, -1), true, 10);
        model.updatePlaceCard("pippo", 27, new Point(0, -2), true, 10);
        model.updatePlaceCard("pippo", 26, new Point(-1, -2), true, 11);

        tui.showTableOfPlayer("pippo");
    }


}







