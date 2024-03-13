package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.Card;

import org.junit.jupiter.api.Assertions;

public class DeckTest {
    @Test
    public void notNull() {
        Deck deck = new Deck("nameImport");
        Assertions.assertNotNull(deck, "Deck is null");
    }

    @Test
    public void draw() {
        Deck deck = new Deck("nameImport");
        Card card = deck.draw();
        Assertions.assertNotNull(card, "Card is null");
    }

    @Test
    public void getKingdomFirstCard() {
        Deck deck = new Deck("nameImport");
        Assertions.assertNotNull(deck.getKingdomFirstCard(), "Kingdom is null");
    }
}
