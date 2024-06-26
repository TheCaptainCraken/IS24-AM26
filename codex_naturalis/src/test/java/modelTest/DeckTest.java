package modelTest;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

public class DeckTest {

    String basePath = "src/test/java/modelTest/test_decks/";

    @Test
    @DisplayName("Test that a deck can be created.")
    public void createDeckTest() {
        Assertions.assertDoesNotThrow(() -> {
            new Deck(basePath + "empty_deck.json");
        });
    }

    @Test
    @DisplayName("Test that an empty deck does not allow draws.")
    public void emptyDeckTest() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Deck deck = new Deck(basePath + "empty_deck.json");
            deck.draw();
        });
    }

    @Test
    @DisplayName("Test that a deck allows to draw.")
    public void drawTest() {
        Assertions.assertDoesNotThrow(() -> {
            Deck deck = new Deck(basePath + "objective_card.json");
            deck.draw();
        });

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Deck deck = new Deck(basePath + "objective_card.json");
            deck.draw();
            deck.draw();
        });
    }

    @Test
    @DisplayName("Test that different types of cards can be created.")
    public void createCardsTest() {
        Assertions.assertDoesNotThrow(() -> {
            new Deck(basePath + "omega_deck.json");
        });
    }
    @Test
    @DisplayName("Test that it's possible to get information about a card in the deck.")
    public void getCardFromPositionInDeck() {
        Assertions.assertDoesNotThrow(() -> {
            Deck deck = new Deck(basePath + "objective_card.json");
            deck.getCard(0);
        });
    }

    @Test
    public void getDeckTest() throws IOException, ParseException {
        Deck deck = new Deck("src/main/java/it/polimi/ingsw/model/decks/objectiveCardsDeck.json", true);
        Assertions.assertEquals(1, deck.getCard(0).getId());
        Assertions.assertEquals(16, deck.getCard(15).getId());

        deck = new Deck("src/main/java/it/polimi/ingsw/model/decks/goldCardsDeck.json", true);
        Assertions.assertEquals(57, deck.getCard(0).getId());
        Assertions.assertEquals(96, deck.getCard(39).getId());

        deck = new Deck("src/main/java/it/polimi/ingsw/model/decks/resourceCardsDeck.json", true);
        Assertions.assertEquals(17, deck.getCard(0).getId());
        Assertions.assertEquals(56, deck.getCard(39).getId());

        deck = new Deck("src/main/java/it/polimi/ingsw/model/decks/startingCardsDeck.json", true);
        Assertions.assertEquals(97, deck.getCard(0).getId());
        Assertions.assertEquals(100, deck.getCard(3).getId());
        Assertions.assertEquals(102, deck.getCard(5).getId());
    }

}
