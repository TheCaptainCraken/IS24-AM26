package modelTest;

import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

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

}
