package it.polimi.ingsw.network.messages.server.gamestart;

import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.ArrayList;

public class ShowObjectiveCards extends ServerMessage {
    ArrayList<Integer> cards;

    public ShowObjectiveCards(ArrayList<Integer> cards) {
        this.cards = cards;
    }

    public ArrayList<Integer> getCards() {
        return cards;
    }
}
