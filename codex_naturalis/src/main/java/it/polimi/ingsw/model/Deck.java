package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck(String nameImport) {
        // nameImport
        // json.foreach
        // switch kind of card aggiungi new ...Card diverse
        this.shuffle();
    }

    /**
     * This method shuffles the deck.
     */
    private void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * This method returns the kingdom of the first card of the deck.
     * 
     * @return the {@link Kingdom} of the first card of the deck.
     */
    public Kingdom getKingdomFirstCard() {
        return cards.get(0).getKingdom();
    }

    /**
     * This method returns the first {@link Card} of the deck and then removes it
     * from the deck.
     * 
     * @return the first {@link Card} of the deck.
     */
    public Card draw() {
        return cards.remove(0);
    }

    /**
     * This method adds a {@link Card} to the deck.
     * 
     * @param card the {@link Card} to add to the deck.
     */
    private void insert(Card card) {
        cards.add(card);
    }
}
