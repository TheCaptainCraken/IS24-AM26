package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Deck;

import org.junit.jupiter.api.Assertions;

public class DeckTest {

    String basePath = "src/test/java/it/polimi/ingsw/test_decks/";

    @Test
    public void createDeckTest() {
        Assertions.assertDoesNotThrow(() -> {
            Deck deck = new Deck(basePath + "empty_deck.json");
        });
    }

    @Test
    public void emptyDeckTest() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Deck deck = new Deck(basePath + "empty_deck.json");
            deck.draw();
        });
    }

    @Test
    public void drawTest() {
        Assertions.assertDoesNotThrow(() -> {
            Deck deck = new Deck(basePath + "objective_card.json");
            deck.draw();
        });
    }
}
