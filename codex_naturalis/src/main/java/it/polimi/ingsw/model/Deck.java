package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck(String nameImport) {
        //nameImport
        //json.foreach
        //switch kind of card aggiungi new ...Card diverse
        this.shuffle();
    }

    private void shuffle(){
        Collections.shuffle(cards);
    }

    public Kingdom getKingdomFirstCard(){//per la GUI
        return cards.get(0).getKingdom();
    }

    public Card draw(){
        return cards.remove(0);
    }

    public void insert(Card card){
        cards.add(card);
    }
}
